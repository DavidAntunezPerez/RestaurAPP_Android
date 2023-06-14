package com.example.restaurapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import com.example.restaurapp.R
import com.example.restaurapp.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.util.Locale

/**
 *  Activity responsible for user sign-up functionality.
 **/
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    // Creating a variable for Firebase Authentication
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the binding object using the generated class from View Binding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the Firebase Authentication instance
        firebaseAuth = FirebaseAuth.getInstance()

        // Set up the click listener for the language change button
        binding.btnChangeLang.setOnClickListener {
            showLanguagePopupMenu(it)
        }

        // Set up the click listener for the "Sign in" text view
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // Set up the click listener for the "Sign Up" button
        binding.signupbutton.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    // Create a user with the provided email and password
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            // If the user creation is successful, navigate to the sign-in activity
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Manage create user exceptions
                            if (it.exception is FirebaseAuthWeakPasswordException) {
                                // If the password is weak
                                val weakPasswordException =
                                    it.exception as FirebaseAuthWeakPasswordException
                                val reason = weakPasswordException.reason
                                val errorMessage = getString(R.string.error_weak_password, reason)

                                Snackbar.make(
                                    binding.root, errorMessage, Snackbar.LENGTH_SHORT
                                ).show()
                            } else if (it.exception is FirebaseAuthInvalidCredentialsException) {
                                // If the email is malformed or invalid
                                Snackbar.make(
                                    binding.root,
                                    R.string.error_invalid_email,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else if (it.exception is FirebaseAuthUserCollisionException) {
                                // If there is already an existing user with that email
                                Snackbar.make(
                                    binding.root, R.string.error_user_exists, Snackbar.LENGTH_SHORT
                                ).show()
                            } else if (it.exception is FirebaseNetworkException) {
                                // If there is a network error
                                Snackbar.make(
                                    binding.root, R.string.error_network, Snackbar.LENGTH_SHORT
                                ).show()
                            } else if (it.exception is FirebaseTooManyRequestsException) {
                                // If there are too many requests
                                Snackbar.make(
                                    binding.root,
                                    R.string.error_too_many_requests,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else if (it.exception is FirebaseAuthException) {
                                // If there is another exception
                                Snackbar.make(
                                    binding.root, it.exception.toString(), Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    // Show a snackbar message if the passwords do not match
                    Snackbar.make(it, R.string.error_password_match, Snackbar.LENGTH_SHORT).show()
                }
            } else if (email.isEmpty()) {
                // Show a snackbar message if the email is empty
                Snackbar.make(it, R.string.error_email_empty, Snackbar.LENGTH_SHORT).show()
            } else if (pass.isEmpty()) {
                // Show a snackbar message if the password is empty
                Snackbar.make(it, R.string.error_password_empty, Snackbar.LENGTH_SHORT).show()
            } else if (confirmPass.isEmpty()) {
                // Show a snackbar message if the confirm password field is empty
                Snackbar.make(it, R.string.error_confirm_password, Snackbar.LENGTH_SHORT).show()
            } else {
                // Show a snackbar message if the credentials are invalid
                Snackbar.make(it, R.string.error_invalid_credentials, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Check if the user is already logged in and navigate to the main activity
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            // If not already logged in, delete the shared preferences variable
            val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
            sharedPreference.edit().clear()
        }
    }

    /**
     * Show the language selection popup menu.
     *
     * @param view The view that triggered the popup menu.
     */
    private fun showLanguagePopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.menu_language, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_english -> {
                    setLocale(Locale.ENGLISH)
                    saveLanguage("en") // Save the language in shared preferences
                    true
                }

                R.id.action_spanish -> {
                    setLocale(Locale("es"))
                    saveLanguage("es") // Save the language in shared preferences
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    /**
     * Save the selected language in shared preferences.
     *
     * @param language The selected language to be saved.
     */
    private fun saveLanguage(language: String) {
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("language", language)
        editor.apply()
    }

    /**
     * Set the app's locale.
     *
     * @param locale The locale to be set.
     */
    private fun setLocale(locale: Locale) {
        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)

        // UPDATE ALL THE NECESSARY STRINGS
        updateStrings()
    }

    /**
     * Update all the necessary strings of the view
     */
    fun updateStrings() {

        // TEXT VIEW SIGN UP
        binding.logInTv.text = getString(R.string.txt_type_signup)

        // SIGN UP BUTTON
        binding.signupbutton.text = getString(R.string.txt_type_signup)

        // ALREADY REGISTERED BUTTON
        binding.textView.text = getString(R.string.txt_sign_in_now)

        // INPUT LAYOUT HINTS
        binding.emailLayout.hint = getString(R.string.txt_type_email)
        binding.passwordLayout.hint = getString(R.string.txt_type_pass)
        binding.confirmPasswordLayout.hint = getString(R.string.txt_type_repass)

    }
}
