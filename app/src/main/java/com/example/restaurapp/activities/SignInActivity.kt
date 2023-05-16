package com.example.restaurapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.restaurapp.R
import com.example.restaurapp.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var gsc: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // INITIALIZING GOOGLE SIGN IN
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        gsc = GoogleSignIn.getClient(this, gso)

        // INITIALIZING FIREBASE AUTH
        firebaseAuth = FirebaseAuth.getInstance()


        // SETTING BUTTON FUNCTIONALITIES

        // NOT REGISTERED OPTION
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        // SIGN IN WITH GOOGLE OPTION
        binding.btnSignInGoogle.setOnClickListener {
            signInGoogle()
        }

        // NORMAL SIGN IN OPTION
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {

                        // MANAGE USER LOGIN EXCEPTIONS
                        if (it.exception is FirebaseAuthInvalidCredentialsException) {
                            // if the provided credential is invalid or expired
                            Snackbar.make(
                                binding.root,
                                "Error. Credentials are not valid or are expired.",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        } else if (it.exception is FirebaseAuthInvalidUserException) {
                            // IF THE USER DOES NOT EXIST OR IS DISABLED
                            Snackbar.make(
                                binding.root,
                                "Error. User does not exist or is disabled",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        } else if (it.exception is FirebaseAuthRecentLoginRequiredException) {
                            // if the user's credential requires additional verification or reauthentication
                            Snackbar.make(
                                binding.root,
                                "Error. User's credential requires additional verification or reauthentication",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        } else if (it.exception is FirebaseAuthUserCollisionException) {
                            // if the credential used for sign-in is already associated with another user account
                            Snackbar.make(
                                binding.root,
                                "Error. Credential used for sign-in is already associated with another user account.",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        } else if (it.exception is FirebaseNetworkException) {
                            // if the app cannot connect to Firebase servers
                            Snackbar.make(
                                binding.root,
                                "Network Error. Cannot connect to the server. Please check your Internet connection or try again later.",
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        } else if (it.exception is FirebaseAuthException) {
                            Snackbar.make(
                                binding.root,
                                it.exception.toString(),
                                Snackbar.LENGTH_SHORT
                            )
                                .show()
                        }

                    }
                }
            } else if (email.isEmpty()) {
                Snackbar.make(it, "Email cannot be empty", Snackbar.LENGTH_SHORT)
                    .show()
            } else if (pass.isEmpty()) {
                Snackbar.make(it, "Password cannot be empty", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(it, "Invalid credentials, please try again", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // IF ALREADY LOGGED IN, SEND USER TO MAIN ACTIVITY
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            // IF NOT ALREADY LOGGED IN, DELETE THE SHARED PREFERENCES VARIABLE
            val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            sharedPreference.edit().clear()
        }
    }

    // SIGN IN WITH GOOGLE FUNCTIONS
    private fun signInGoogle() {
        val signInIntent = gsc.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResults(task)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent: Intent = Intent(this, MainActivity::class.java)
                intent.putExtra("email", account.email)
                intent.putExtra("name", account.displayName)
                startActivity(intent)
            } else {
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}