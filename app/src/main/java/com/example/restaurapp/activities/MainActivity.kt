package com.example.restaurapp.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.restaurapp.R
import com.example.restaurapp.databinding.ActivityMainBinding
import com.example.restaurapp.fragments.MainFragment
import com.example.restaurapp.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        // SET THE LANGUAGE SAVED IN SHARED PREFERENCES
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val language = sharedPreference.getString("language", null)

        if (language != null) {
            val locale = when (language) {
                "Spanish" -> Locale("es")
                else -> Locale.ENGLISH
            }
            setLocale(locale, false)
        }

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // GET USER UID
        val userUID = firebaseAuth.currentUser?.uid

        // STORE USER UID IN SHARED PREFERENCES
        val editor = sharedPreference.edit()
        // PUTTING IT INTO A STRING
        editor.putString("userUID", userUID)
        editor.apply()

        // SET THE BOTTOM NAVIGATION
        bottomNavigationView = binding.bottomNav
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_item1 -> {
                    loadFragment(MainFragment())
                    true
                }

                R.id.menu_item2 -> {
                    loadFragment(SettingsFragment())
                    true
                }

                else -> false
            }
        }

        // Set the initial fragment
        loadFragment(MainFragment())

    }

    // FUNCTION TO LOAD A FRAGMENT FOR NAVIGATION
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_main, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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
}