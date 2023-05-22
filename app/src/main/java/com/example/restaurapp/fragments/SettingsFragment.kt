package com.example.restaurapp.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.restaurapp.R
import com.example.restaurapp.activities.SignInActivity
import com.example.restaurapp.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var languageSpinner: Spinner

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Sign out button function
        binding.btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), SignInActivity::class.java))
            requireActivity().finish()
        }

        // Initialize shared preferences
        sharedPreferences =
            requireContext().getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        // SET UP THE LANGUAGE SPINNER
        languageSpinner = binding.languageSpinner

        // Set up the adapter for the language spinner
        val languages = resources.getStringArray(R.array.languages)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        // Get the current language from shared preferences or use the default language
        val currentLanguage = sharedPreferences.getString("language", getDefaultLanguage())

        // Set the selected item in the language spinner to match the current language
        val languageIndex = currentLanguage?.let { getLanguageIndex(it) } ?: 0
        languageSpinner.setSelection(languageIndex)

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

        // Save the settings or update the user profile with the entered information
        // Implement your logic here
    }

    private fun saveChangeLang() {
        val defaultLanguage = getDefaultLanguage()

        val selectedLanguage = languageSpinner.selectedItem?.toString() ?: defaultLanguage
        val languageIndex = getLanguageIndex(selectedLanguage)
        languageSpinner.setSelection(languageIndex)

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
        return when (Locale.getDefault()) {
            Locale.ENGLISH -> getString(R.string.english_language)
            Locale("es") -> getString(R.string.spanish_language)
            else -> getString(R.string.english_language) // Set English as the default language
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
