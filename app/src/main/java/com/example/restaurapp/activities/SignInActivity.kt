package com.example.restaurapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var gsc: GoogleSignInClient
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // INITIALIZING GOOGLE SIGN IN
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        gsc = GoogleSignIn.getClient(this, gso)

        // INITIALIZING FIREBASE AUTH AND FIRE STORE
        firebaseAuth = FirebaseAuth.getInstance()

        firestore = FirebaseFirestore.getInstance()


        // SET LANG CHANGE BUTTON
        binding.btnChangeLang.setOnClickListener {
            showLanguagePopupMenu(it)
        }

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
                        checkAndCreateUserDocument()
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
                            ).show()
                        } else if (it.exception is FirebaseAuthInvalidUserException) {
                            // IF THE USER DOES NOT EXIST OR IS DISABLED
                            Snackbar.make(
                                binding.root,
                                "Error. User does not exist or is disabled",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else if (it.exception is FirebaseAuthRecentLoginRequiredException) {
                            // if the user's credential requires additional verification or reauthentication
                            Snackbar.make(
                                binding.root,
                                "Error. User's credential requires additional verification or reauthentication",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else if (it.exception is FirebaseAuthUserCollisionException) {
                            // if the credential used for sign-in is already associated with another user account
                            Snackbar.make(
                                binding.root,
                                "Error. Credential used for sign-in is already associated with another user account.",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else if (it.exception is FirebaseNetworkException) {
                            // if the app cannot connect to Firebase servers
                            Snackbar.make(
                                binding.root,
                                "Network Error. Cannot connect to the server. Please check your Internet connection or try again later.",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else if (it.exception is FirebaseAuthException) {
                            Snackbar.make(
                                binding.root, it.exception.toString(), Snackbar.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
            } else if (email.isEmpty()) {
                Snackbar.make(it, "Email cannot be empty", Snackbar.LENGTH_SHORT).show()
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

        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        // IF ALREADY LOGGED IN, SEND USER TO MAIN ACTIVITY
        if (firebaseAuth.currentUser != null) {
            // SET THE LANGUAGE SAVED IN SHARED PREFERENCES
            val language = sharedPreference.getString("language", null)

            if (language != null) {
                val locale = when (language) {
                    "Spanish" -> Locale("es")
                    else -> Locale.ENGLISH
                }
                setLocale(locale, false)
            }

            // CHECK IF THERE IS AN USER DOCUMENT ALREADY CREATED
            checkAndCreateUserDocument()

            // NAVIGATE TO MAIN ACTIVITY
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            // IF NOT ALREADY LOGGED IN, DELETE THE SHARED PREFERENCES VARIABLE
            sharedPreference.edit().clear()
        }
    }

    // CREATION OF USER DOCUMENT IN CASE IT DOES NOT EXISTS
    private fun checkAndCreateUserDocument() {
        val currentUser = firebaseAuth.currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            val userRef = firestore.collection("users").document(uid)
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {

                        // User document already exists

                    } else {

                        // User document does not exist, create a new one with default values
                        val defaultValues = hashMapOf(
                            "name" to "Anonymous",
                            "description" to "",
                            "location" to "",
                            "image" to ""
                        )

                        userRef.set(defaultValues)
                    }
                }
            }
        }
    }

    // SET UP THE MENU
    // Show a popup menu with language options
    private fun showLanguagePopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_language, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_english -> {
                    setLocale(Locale.ENGLISH, true)
                    saveLanguage("en") // Save the language in shared preferences
                    true
                }

                R.id.action_spanish -> {
                    setLocale(Locale("es"), true)
                    saveLanguage("es") // Save the language in shared preferences
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }


    // Set the app's locale
    private fun setLocale(locale: Locale, onRecreate: Boolean) {
        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        if (onRecreate) {
            // Restart the activity for the locale change to take effect
            recreate()
        }
    }

    // Save the selected language in shared preferences
    private fun saveLanguage(language: String) {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("language", language)
        editor.apply()
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
                checkAndCreateUserDocument()
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