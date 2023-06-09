package com.example.restaurapp.fragments

import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.Manifest
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.restaurapp.R
import com.example.restaurapp.activities.MainActivity
import com.example.restaurapp.activities.SignInActivity
import com.example.restaurapp.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.Locale

/**
 *  Fragment that manages the settings of the application
 */
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

    /**
     * Inflates the layout of the fragment.
     * @param inflater The layout inflater.
     * @param container The parent view.
     * @param savedInstanceState The saved instance state.
     * @return The root view of the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called when the fragment is resumed. Retrieves the selected language from shared preferences
     * and updates the language spinner accordingly.
     */
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

        changeLangStrings()

    }

    /**
     * Called when the view is created. Initializes Firebase Auth, Firebase Firestore, and shared preferences.
     * Sets up click listeners for buttons and initializes the language spinner.
     * Retrieves user data from Firestore and updates the UI with the data.
     * @param view The created view.
     * @param savedInstanceState The saved instance state.
     */
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

        // LOADING ANIMATIONS
        // LOAD RV
        val loadingImageView: ImageView = binding.loadingAnimation
        // LOADING SPINNER
        binding.loadingAnimation.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(), R.anim.loading_anim
            )
        )

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

                        // HIDE LOADING ANIMATION
                        stopAnimation(loadingImageView)
                    }
                } else {
                    // Document does not exist
                    Log.d("SettingsFragment", "User document does not exist")

                    // HIDE LOADING ANIMATION
                    stopAnimation(loadingImageView)
                }

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

        changeLangStrings()

    }

    /**
     * Stops the animation and hides the given ImageView.
     *
     * @param imageView The ImageView to stop the animation and hide.
     */
    private fun stopAnimation(imageView: ImageView) {
        imageView.clearAnimation() // Clear the animation
        imageView.visibility = View.GONE // Hide the ImageView
    }

    /**
     * Saves the entered settings and updates the user document in Firestore.
     */
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
            Snackbar.make(
                binding.root, getString(R.string.settings_saved_message), Snackbar.LENGTH_SHORT
            ).show()

            // UPDATE THE FRAGMENT VARIABLE SETTINGS
            userName = name
            userDescription = description
            userLocation = location

            // UPDATE THE UI WITH THE FIREBASE DATA
            updateUIWithUserData()

            // Perform any additional actions or UI updates
        }.addOnFailureListener { exception ->
            // Show error message using Snackbar
            Snackbar.make(
                binding.root,
                getString(R.string.error_saving_settings, exception),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Saves the selected language as the app's language and updates the configuration accordingly.
     */
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

        val mainActivity = activity as? MainActivity
        mainActivity?.updateBottomNavigationBarTitles()

        // CHANGE THE STINGS OF THE VIEW
        changeLangStrings()
    }

    /**
     *  Updates the necessary strings of the fragment when the language is changed
     */
    private fun changeLangStrings() {

        // LOGOUT BUTTON
        binding.btnLogout.text = getString(R.string.log_out_fragment_settings)

        // LANGUAGE CHOOSE TEXT
        binding.languageChooseText.text = getString(R.string.settings_language)

        // SAVE BUTTON
        binding.saveButton.text = getString(R.string.settings_save)

        // INPUT LAYOUTS HINTS
        binding.nameInputLayout.hint = getString(R.string.settings_name)
        binding.locationInputLayout.hint = getString(R.string.settings_location)
        binding.descriptionInputLayout.hint = getString(R.string.settings_description)

    }


    /**
     * Returns the index of the given language in the list of supported languages.
     *
     * @param language The language to find the index of.
     * @return The index of the language in the list of supported languages.
     */
    private fun getLanguageIndex(language: String): Int {
        val languages = resources.getStringArray(R.array.languages)
        return languages.indexOf(language)
    }

    /**
     * Returns the default language based on the device's locale.
     *
     * @return The default language as a string.
     */

    private fun getDefaultLanguage(): String {
        return when (Locale.getDefault().language) {
            Locale.ENGLISH.language -> getString(R.string.english_language)
            Locale("es").language -> getString(R.string.spanish_language)
            else -> getString(R.string.english_language) // Set English as the default language
        }
    }

    /**
     * Updates the UI elements with the retrieved user data.
     */
    private fun updateUIWithUserData() {
        if (_binding != null) {
            // Update UI elements with the retrieved user data
            binding.nameEditText.setText(userName)
            binding.descriptionEditText.setText(userDescription)
            binding.locationEditText.setText(userLocation)
        }
    }

    /**
     * Loads the user's image into the profile image view.
     */
    private fun loadImage() {

        if (_binding != null) {
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
    }

    /**
     * Displays a dialog to allow the user to select an image option: choose from gallery, take picture with camera,
     * or remove picture.
     */
    private fun showImageSelectionDialog() {
        val options = arrayOf(
            getString(R.string.choose_image_gallery),
            getString(R.string.take_picture_camera),
            getString(R.string.delete_profile_picture)
        )

        MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.edit_restaurant_image_title))
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> {
                        // Choose image from gallery option selected
                        chooseImageFromGallery()
                    }

                    1 -> {
                        // Take picture with camera option selected
                        takePictureWithCamera()
                    }

                    2 -> {
                        // Remove picture option
                        removeImage()
                    }
                }
                dialog.dismiss()
            }.setIcon(R.drawable.ic_image_edit).show()
    }

    /**
     * Removes the user's image by updating the user document in Firestore with an empty image path.
     */
    private fun removeImage() {
        // Update the user document in Firestore with an empty image path
        val userUID = sharedPreferences.getString("userUID", "userUID")
        val userDocumentRef = firestore.collection("users").document(userUID!!)

        userDocumentRef.update("image", "").addOnSuccessListener {
            // Show success message using Snackbar
            Snackbar.make(
                binding.root, getString(R.string.image_removed_successfully), Snackbar.LENGTH_SHORT
            ).show()

            // Update the userImage variable
            userImage = ""

            // Load the default image
            loadImage()
        }.addOnFailureListener { exception ->
            // Show error message using Snackbar
            Snackbar.make(
                binding.root,
                getString(R.string.error_removing_image, exception),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private companion object {
        private const val REQUEST_PICK_IMAGE = 1002
        private const val REQUEST_CAPTURE_IMAGE = 1004
        private const val REQUEST_CAMERA_PERMISSION = 1003
    }

    /**
     * Opens the gallery to choose an image.
     */
    private fun chooseImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE)
    }

    /**
     * Handles the result of an activity.
     *
     * @param requestCode The request code.
     * @param resultCode The result code.
     * @param data The intent data.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_PICK_IMAGE -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        uploadImageToFirebase(selectedImageUri)
                    } else {
                        // Handle null selected image URI
                        Snackbar.make(
                            binding.root,
                            getString(R.string.error_selecting_image),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                REQUEST_CAPTURE_IMAGE -> {
                    val capturedImage = data?.extras?.get("data") as? Bitmap
                    if (capturedImage != null) {
                        // Convert the captured image to a Uri
                        val imageUri = getImageUri(requireContext(), capturedImage)
                        if (imageUri != null) {
                            uploadImageToFirebase(imageUri)
                        } else {
                            // Handle null image URI
                            Snackbar.make(
                                binding.root,
                                getString(R.string.error_capturing_image),
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        // Handle null captured image
                        Snackbar.make(
                            binding.root,
                            getString(R.string.error_capturing_image),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    /**
     * Uploads the image to Firebase Storage.
     *
     * @param imageUri The URI of the image to upload.
     */
    private fun uploadImageToFirebase(imageUri: Uri) {
        // Upload the image to Firebase Storage

        // LOADING ANIMATIONS
        // LOAD RV
        val loadingImageView: ImageView = binding.loadingAnimation
        // LOADING SPINNER
        binding.loadingAnimation.startAnimation(
            AnimationUtils.loadAnimation(
                requireContext(), R.anim.loading_anim
            )
        )

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
                    binding.root,
                    getString(R.string.image_uploaded_successfully),
                    Snackbar.LENGTH_SHORT
                ).show()

                // STOP THE LOADING ANIMATION
                stopAnimation(loadingImageView)

                // Update the userImage variable with the new image path
                userImage = imagePath

                // Load the new image
                loadImage()
            }.addOnFailureListener { exception ->
                // Show error message using Snackbar
                Snackbar.make(
                    binding.root,
                    getString(R.string.error_uploading_image, exception),
                    Snackbar.LENGTH_SHORT
                ).show()

                // STOP THE LOADING ANIMATION
                stopAnimation(loadingImageView)
            }
        }.addOnFailureListener { exception ->
            // Show error message using Snackbar
            Snackbar.make(
                binding.root,
                getString(R.string.error_uploading_image, exception),
                Snackbar.LENGTH_SHORT
            ).show()

            // STOP THE LOADING ANIMATION
            stopAnimation(loadingImageView)
        }
    }

    /**
     * Checks if the CAMERA permission is already granted. If granted, it starts the camera. Otherwise, it requests the permission.
     */
    private fun takePictureWithCamera() {
        // Check if the CAMERA permission is already granted
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted, start the camera
            launchCameraApp()
        } else {
            // Permission is not granted, request the permission
            requestCameraPermission()
        }
    }

    /**
     * Launches the camera application to capture an image.
     */
    private fun launchCameraApp() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CAPTURE_IMAGE)
    }

    /**
     * Requests the CAMERA permission.
     */
    private fun requestCameraPermission() {
        // Request the CAMERA permission
        requestPermissions(
            arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION
        )
    }

    /**
     * Retrieves the URI of the captured image.
     *
     * @param context The application context.
     * @param image The captured image bitmap.
     * @return The URI of the captured image.
     */
    private fun getImageUri(context: Context, image: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver, image, "Image", null
        )
        return Uri.parse(path)
    }

    /**
     * Handles the result of the permission request.
     *
     * @param requestCode The request code.
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, start the camera
                launchCameraApp()
            } else {
                // Permission is denied
                showPermissionDeniedDialog()
            }
        }
    }

    /**
     * Shows a dialog indicating that camera permission is denied.
     */
    private fun showPermissionDeniedDialog() {
        MaterialAlertDialogBuilder(requireActivity()).setTitle(getString(R.string.camera_permission_required))
            .setMessage(getString(R.string.camera_permission_message))
            .setPositiveButton(getString(R.string.go_to_settings)) { dialog, _ ->
                dialog.dismiss()
                openAppSettings()
            }.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }.setIcon(android.R.drawable.ic_dialog_alert).show()
    }

    /**
     * Opens the application settings screen.
     */
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
