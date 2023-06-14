package com.example.restaurapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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

/**
 * Activity responsible for user sign-in functionality.
 */
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var gsc: GoogleSignInClient
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initializing Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        gsc = GoogleSignIn.getClient(this, gso)

        // Initializing Firebase Auth and Firestore
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Set language change button functionality
        binding.btnChangeLang.setOnClickListener {
            showLanguagePopupMenu(it)
        }

        // Setting button functionalities

        // Not registered option
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Sign in with Google option
        binding.btnSignInGoogle.setOnClickListener {
            signInGoogle()
        }

        // Normal sign in option
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
                        // Manage user login exceptions
                        if (it.exception is FirebaseAuthInvalidCredentialsException) {
                            // If the provided credential is invalid or expired
                            Snackbar.make(
                                binding.root,
                                getString(R.string.invalid_credentials),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else if (it.exception is FirebaseAuthInvalidUserException) {
                            // If the user does not exist or is disabled
                            Snackbar.make(
                                binding.root,
                                getString(R.string.user_not_exist),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else if (it.exception is FirebaseAuthRecentLoginRequiredException) {
                            // If the user's credential requires additional verification or reauthentication
                            Snackbar.make(
                                binding.root,
                                getString(R.string.additional_verification_user),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else if (it.exception is FirebaseAuthUserCollisionException) {
                            // If the credential used for sign-in is already associated with another user account
                            Snackbar.make(
                                binding.root,
                                getString(R.string.credential_associated_account),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else if (it.exception is FirebaseNetworkException) {
                            // If the app cannot connect to Firebase servers
                            Snackbar.make(
                                binding.root,
                                getString(R.string.network_error),
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
                Snackbar.make(it, getString(R.string.email_cannot_empty), Snackbar.LENGTH_SHORT)
                    .show()
            } else if (pass.isEmpty()) {
                Snackbar.make(it, getString(R.string.password_cannot_empty), Snackbar.LENGTH_SHORT)
                    .show()
            } else {
                Snackbar.make(
                    it, getString(R.string.invalid_credentials_try_again), Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    /** On start of the activity */
    override fun onStart() {
        super.onStart()

        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        // Check if the user is already logged in
        if (firebaseAuth.currentUser != null) {
            // The user is already logged in

            // Retrieve the language saved in shared preferences
            val language = sharedPreference.getString("language", null)

            if (language != null) {
                // A language is saved in shared preferences

                // Determine the locale based on the saved language
                val locale = when (language) {
                    "Spanish" -> Locale("es")
                    else -> Locale.ENGLISH
                }

                // Set the locale without recreating the activity
                setLocale(locale)
            }

            // Check if there is an existing user document
            checkAndCreateUserDocument()

            // Navigate to the main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            // The user is not already logged in

            // Delete the shared preferences variable
            sharedPreference.edit().clear().apply()
        }
    }

    /**
     * Check and create a user document in case it does not exist.
     */
    private fun checkAndCreateUserDocument() {
        // Get the current user and their UID
        val currentUser = firebaseAuth.currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            // Create a reference to the user document in the "users" collection
            val userRef = firestore.collection("users").document(uid)

            // Check if the user document exists
            userRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        // User document already exists
                        // Perform any necessary actions or logic here
                    } else {
                        // User document does not exist, create a new one with default values

                        // Define the default values for the user document
                        val defaultValues = hashMapOf(
                            "name" to "", "description" to "", "location" to "", "image" to ""
                        )

                        // Set the default values in the user document
                        userRef.set(defaultValues)
                    }
                }
            }
        }
    }

    /**
     * Set up the menu.
     * Show a popup menu with language options.
     *
     * @param view The view associated with the menu.
     */
    private fun showLanguagePopupMenu(view: View) {
        // Create a new instance of PopupMenu
        val popupMenu = PopupMenu(this, view)

        // Inflate the menu resource file into the PopupMenu's menu
        popupMenu.menuInflater.inflate(R.menu.menu_language, popupMenu.menu)

        // Set the click listener for menu items
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_english -> {
                    // Set the locale to English and recreate the activity
                    setLocale(Locale.ENGLISH)

                    // Save the selected language in shared preferences
                    saveLanguage("en")

                    true
                }

                R.id.action_spanish -> {
                    // Set the locale to Spanish and recreate the activity
                    setLocale(Locale("es"))

                    // Save the selected language in shared preferences
                    saveLanguage("es")

                    true
                }

                else -> false
            }
        }

        // Show the PopupMenu
        popupMenu.show()
    }

    /**
     * Set the app's locale.
     *
     * @param locale The desired locale.
     *
     * */
    private fun setLocale(locale: Locale) {
        // Get the resources object for the current context
        val resources = resources

        // Get the configuration object from the resources
        val configuration = resources.configuration

        // Set the locale of the configuration to the specified locale
        configuration.setLocale(locale)

        // Update the configuration and display metrics of the resources
        resources.updateConfiguration(configuration, resources.displayMetrics)

        // UPDATE ALL THE NECESSARY STRINGS
        updateStrings()
    }

    /**
     * Update all the necessary strings of the view
     */
    fun updateStrings() {

        // LOG IN TEXT
        binding.logInTv.text = getString(R.string.txt_type_signin)

        // SIGN IN BUTTON
        binding.button.text = getString(R.string.txt_type_signin)

        // NOT REGISTERED BUTTON
        binding.textView.text = getString(R.string.txt_sign_up_now)

        // GOOGLE SIGN IN BUTTON
        binding.btnSignInGoogle.text = getString(R.string.txt_type_google)

        // INPUT LAYOUT HINTS
        binding.passwordLayout.hint = getString(R.string.txt_type_pass)
        binding.emailLayout.hint = getString(R.string.txt_type_email)

    }

    /**
     * Save the selected language in shared preferences.
     *
     * @param language The selected language.
     */
    private fun saveLanguage(language: String) {
        // Retrieve the shared preferences with the specified name and mode
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        // Retrieve the editor for modifying the shared preferences
        val editor = sharedPreference.edit()

        // Store the selected language in the shared preferences
        editor.putString("language", language)

        // Apply the changes to the shared preferences
        editor.apply()
    }

    /**
     * Sign in with Google.
     * Retrieve the sign-in intent from the GoogleSignInClient and launch the sign-in intent.
     */
    private fun signInGoogle() {
        // Retrieve the sign-in intent from the GoogleSignInClient
        val signInIntent = gsc.signInIntent

        // Launch the sign-in intent using the launcher
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // Check if the sign-in activity was successful
            if (result.resultCode == Activity.RESULT_OK) {
                // Retrieve the task containing the signed-in account from the result data
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                // Handle the sign-in task results
                handleResults(task)
            }
        }

    /**
     * Handles the task results of signing in with Google.
     *
     * @param task The Task containing the result of the sign-in process.
     */
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        // Check if the task was successful
        if (task.isSuccessful) {
            // Retrieve the GoogleSignInAccount from the task result
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                // If the account is not null, update the UI with the account information
                updateUI(account)
            }
        } else {
            // If the task was not successful, display an error toast with the exception message
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Updates the UI based on the GoogleSignInAccount information.
     *
     * @param account The GoogleSignInAccount containing the user's account information.
     */
    private fun updateUI(account: GoogleSignInAccount) {
        // Get the ID token from the GoogleSignInAccount
        val idToken = account.idToken

        // Create a GoogleAuthProvider credential using the ID token
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        // Sign in with the credential using Firebase Auth
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign-in success
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // Sign-in failed
                Toast.makeText(
                    this, task.exception.toString(), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
