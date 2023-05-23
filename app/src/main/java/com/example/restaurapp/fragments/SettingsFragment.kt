package com.example.restaurapp.fragments

import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        // Handle edit image button
        binding.btnEditImgSettings.setOnClickListener {
            showImageSelectionDialog()
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

    private fun showImageSelectionDialog() {
        val options = arrayOf(
            "\uD83D\uDCC1 Choose image from gallery", "\uD83D\uDCF7 Take picture with camera"
        )

        MaterialAlertDialogBuilder(requireContext()).setTitle("Edit your Restaurant image")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        // Choose image from gallery option selected
                        chooseImageFromGallery()
                        loadImage()
                    }

                    1 -> {
                        // Take picture with camera option selected
                        takePictureWithCamera()
                        loadImage()
                    }
                }
                dialog.dismiss()
            }.setIcon(R.drawable.ic_image_edit).show()
    }


    private companion object {
        private const val REQUEST_GALLERY_PERMISSION = 1001
        private const val REQUEST_PICK_IMAGE = 1002
    }

    private fun chooseImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_GALLERY_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    // Permission denied by the user, handle it as needed
                    // For example, show a message or disable the gallery option
                    Snackbar.make(
                        binding.root,
                        "Permission denied. Unable to access storage without permissions.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                uploadImageToFirebase(selectedImageUri)
            }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        // Upload the image to Firebase Storage

        // REFERENCING FIREBASE STORAGE
        val storageRef = Firebase.storage.reference

        // CREATING THE PATH TO THE IMAGE WITH THE UID + IMAGE NAME
        val userUID = sharedPreferences.getString("userUID", "userUID")
        val fileName = "$userUID.jpg"

        // Specify the folder in which the image will be uploaded (e.g., "images/")
        val folderName = "images/"
        val pathReference = storageRef.child("$folderName$fileName")

        // UPLOADING THE IMAGE TO FIREBASE STORAGE
        pathReference.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            // Get the relative path of the uploaded image
            val imagePath = "$folderName$fileName"

            // Update the user document in Firestore with the image path
            val userDocumentRef = firestore.collection("users").document(userUID!!)
            userDocumentRef.update("image", imagePath).addOnSuccessListener {
                // Show success message using Snackbar
                Snackbar.make(
                    binding.root, "Image uploaded successfully", Snackbar.LENGTH_SHORT
                ).show()

                // Update the userImage variable with the new image path
                userImage = imagePath

                // Load the new image
                loadImage()
            }.addOnFailureListener { exception ->
                // Show error message using Snackbar
                Snackbar.make(
                    binding.root, "Error uploading image, $exception", Snackbar.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener { exception ->
            // Show error message using Snackbar
            Snackbar.make(
                binding.root, "Error uploading image, $exception", Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun takePictureWithCamera() {
        // TODO: Implement the logic for taking a picture with the camera
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
