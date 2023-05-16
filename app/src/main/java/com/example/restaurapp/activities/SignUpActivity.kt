package com.example.restaurapp.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.restaurapp.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    // creating variable for FireBase Authentication
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.signupbutton.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                        } else {
                            // MANAGE CREATE USER EXCEPTIONS
                            if (it.exception is FirebaseAuthWeakPasswordException) {
                                // IF THE PASSWORD IS WEAK
                                val weakPasswordException =
                                    it.exception as FirebaseAuthWeakPasswordException
                                val reason = weakPasswordException.reason
                                val errorMessage = "Error. Password is too weak. $reason"

                                Snackbar.make(
                                    binding.root,
                                    errorMessage,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else if (it.exception is FirebaseAuthInvalidCredentialsException) {
                                // IF EMAIL IS MALFORMED OR INVALID
                                Snackbar.make(
                                    binding.root,
                                    "Error. Email is malformed or invalid.",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else if (it.exception is FirebaseAuthUserCollisionException) {
                                // IF THERE IS ALREADY AN EXISTING USER WITH THAT MAIL
                                Snackbar.make(
                                    binding.root,
                                    "Error. User already exists with the given email.",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else if (it.exception is FirebaseNetworkException) {
                                // IF IS A NETWORK ERROR
                                Snackbar.make(
                                    binding.root,
                                    "Network Error. Cannot connect to the server. Please check your Internet connection or try again later.",
                                    Snackbar.LENGTH_SHORT
                                ).show()

                            } else if (it.exception is FirebaseTooManyRequestsException) {
                                // IF THERE IS TOO MANY REQUESTS
                                Snackbar.make(
                                    binding.root,
                                    "Error. You have sent too many requests, please try again later.",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else if (it.exception is FirebaseAuthException) {
                                // IF IS ANOTHER EXCEPTION
                                Snackbar.make(
                                    binding.root,
                                    it.exception.toString(),
                                    Snackbar.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                    }

                } else {
                    Snackbar.make(it, "Password does not match", Snackbar.LENGTH_SHORT).show()
                }
            } else if (email.isEmpty()) {
                Snackbar.make(it, "Email cannot be empty", Snackbar.LENGTH_SHORT).show()
            } else if (pass.isEmpty()) {
                Snackbar.make(it, "Password cannot be empty", Snackbar.LENGTH_SHORT).show()
            } else if (confirmPass.isEmpty()) {
                Snackbar.make(it, "Please confirm your password", Snackbar.LENGTH_SHORT).show()
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
}