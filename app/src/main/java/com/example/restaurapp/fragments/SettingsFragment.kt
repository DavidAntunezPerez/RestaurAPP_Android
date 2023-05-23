package com.example.restaurapp.fragments

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.restaurapp.R
import com.example.restaurapp.activities.SignInActivity
import com.example.restaurapp.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var languageSpinner: Spinner

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var firestore: FirebaseFirestore

    // FIRE STORE RETRIEVED VALUES
    private var userImage: String = ""
    private var userDescription: String = ""
    private var userName: String = ""
    private var userLocation: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // Retrieve the selected language from shared preferences
        val currentLanguage = sharedPreferences.getString("language", null)

        Log.i("LANGUAGE GET DEFAULT RESUME", Locale.getDefault().language)
        Log.i("LANGUAGE GET DEFAULT RESUME", currentLanguage.toString())

        // Set the selected item in the language spinner to match the current language
        val languageIndex = currentLanguage?.let { getLanguageIndex(it) } ?: 0

        languageSpinner.setSelection(languageIndex)

        Log.i("LANGUAGE GET DEFAULT RESUME SPINNER", "$languageIndex")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize Firebase Fire store
        firestore = FirebaseFirestore.getInstance()

        // Sign out button function
        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), SignInActivity::class.java))
            requireActivity().finish()
        }

        // Initialize shared preferences
        sharedPreferences =
            requireContext().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        // GET THE USER UID
        val userUID = sharedPreferences.getString("userUID", "userUID")

        // SET UP THE LANGUAGE SPINNER
        languageSpinner = binding.languageSpinner

        // Set up the adapter for the language spinner
        val languages = resources.getStringArray(R.array.languages)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        // Get the current language from shared preferences or use the default language
        val currentLanguage = sharedPreferences.getString("language", null)

        // Set the selected item in the language spinner to match the current language
        val languageIndex = currentLanguage?.let { getLanguageIndex(it) } ?: 0
        languageSpinner.setSelection(languageIndex)

        // RETRIEVE THE USER DATA FROM FIREBASE
        // Retrieve user document from Firestore
        val userDocumentRef = firestore.collection("users").document(userUID!!)
        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val userData = documentSnapshot.data
                if (userData != null) {
                    userImage = userData["image"] as? String ?: ""
                    userDescription = userData["description"] as? String ?: ""
                    userName = userData["name"] as? String ?: ""
                    userLocation = userData["location"] as? String ?: ""

                    // Update UI with the retrieved values
                    updateUIWithUserData()

                    // LOAD IMAGE
                    loadImage()
                }
            } else {
                // Document does not exist
                Log.d("SettingsFragment", "User document does not exist")
            }
        }

        // Handle save button click
        binding.saveButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun saveSettings() {
        val name = binding.nameEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val location = binding.locationEditText.text.toString()

        saveChangeLang()

        // Update the user document in Firestore with the entered information
        val userUID = sharedPreferences.getString("userUID", "userUID")
        val userDocumentRef = firestore.collection("users").document(userUID!!)

        val userData: MutableMap<String, Any?> = hashMapOf(
            "name" to name, "description" to description, "location" to location
        )

        userDocumentRef.update(userData).addOnSuccessListener {
            // Show success message using Snackbar
            Snackbar.make(binding.root, "Settings saved successfully", Snackbar.LENGTH_SHORT).show()

            // UPDATE THE FRAGMENT VARIABLE SETTINGS
            userName = name
            userDescription = description
            userLocation = location

            // UPDATE THE UI WITH THE FIREBASE DATA
            updateUIWithUserData()

            // Perform any additional actions or UI updates
        }.addOnFailureListener { exception ->
            // Show error message using Snackbar
            Snackbar.make(binding.root, "Error saving settings, $exception", Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    private fun saveChangeLang() {
        val defaultLanguage = getDefaultLanguage()

        val selectedLanguage = languageSpinner.selectedItem?.toString() ?: defaultLanguage

        // Set the app's language based on the selected language
        val locale = when (selectedLanguage) {
            getString(R.string.english_language) -> Locale.ENGLISH
            getString(R.string.spanish_language) -> Locale("es")
            // Add more cases for additional language options if needed
            else -> Locale.ENGLISH
        }

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save the selected language to shared preferences
        sharedPreferences.edit().putString("language", selectedLanguage).apply()

    }

    private fun getLanguageIndex(language: String): Int {
        val languages = resources.getStringArray(R.array.languages)
        return languages.indexOf(language)
    }

    private fun getDefaultLanguage(): String {
        return when (Locale.getDefault().language) {
            Locale.ENGLISH.language -> getString(R.string.english_language)
            Locale("es").language -> getString(R.string.spanish_language)
            else -> getString(R.string.english_language) // Set English as the default language
        }
    }

    private fun updateUIWithUserData() {
        // Update UI elements with the retrieved user data
        binding.nameEditText.setText(userName)
        binding.descriptionEditText.setText(userDescription)
        binding.locationEditText.setText(userLocation)
    }

    private fun loadImage() {
        // Load user image using the userImage variable

        // Apply fade-in animation to the ImageView
        val fadeInAnimator = ObjectAnimator.ofFloat(binding.profileImageView, "alpha", 0f, 1f)
        fadeInAnimator.duration = 500 // Animation duration in milliseconds

        // LOAD IMAGES

        if (userImage.isNotEmpty()) {
            // IF A PATH IS SET IN FIRE STORE, LOAD IMAGE FROM STORAGE

            // REFERENCING FIREBASE STORAGE
            val storageRef = Firebase.storage.reference

            // REFERENCING THE RESULTING PATH INTO A CHILD
            val pathReference = storageRef.child(userImage)

            // SET THE MAX SIZE OF THE IMAGE (5MB IN THIS CASE)
            val fileMaxSize: Long = 5 * 1000000

            pathReference.getBytes(fileMaxSize).addOnSuccessListener { path ->

                // LOADING THE IMAGE WITH GLIDE USING THE PATH GIVEN
                Glide.with(this).load(path).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .override(250, 250).centerCrop().into(binding.profileImageView)

                fadeInAnimator.start() // Start the fade-in animation

            }
        } else {
            // LOADING THE IMAGE WITH GLIDE USING THE DEFAULT IMAGE
            Glide.with(this).load(R.drawable.restaurant_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL).override(250, 250).centerCrop()
                .into(binding.profileImageView)

            fadeInAnimator.start() // Start the fade-in animation
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
