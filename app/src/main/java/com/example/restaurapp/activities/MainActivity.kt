package com.example.restaurapp.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.restaurapp.R
import com.example.restaurapp.databinding.ActivityMainBinding
import com.example.restaurapp.fragments.ComListFragment
import com.example.restaurapp.fragments.SelectTableFragment
import com.example.restaurapp.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

/**
 * Main activity of the project
 */
class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        // CHANGE THE STATUS BAR COLOR
        window.statusBarColor = ContextCompat.getColor(this, R.color.app_secondary_pink)

        // Set the language saved in shared preferences
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

        // Get user UID
        val userUID = firebaseAuth.currentUser?.uid

        // Store user UID in shared preferences
        val editor = sharedPreference.edit()
        editor.putString("userUID", userUID)
        editor.apply()

        // Set up the bottom navigation
        bottomNavigationView = binding.bottomNav
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_item1 -> {
                    loadFragment(SelectTableFragment())
                    true
                }

                R.id.menu_item2 -> {
                    loadFragment(ComListFragment())
                    true
                }

                R.id.menu_item3 -> {
                    loadFragment(SettingsFragment())
                    true
                }

                else -> false
            }
        }

        // Set the initial fragment
        loadFragment(SelectTableFragment())
    }

    /**
     * Function to load a fragment for navigation.
     * Replaces the current fragment with the specified fragment and adds the transaction to the back stack.
     * @param fragment The fragment to be loaded.
     */
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_main, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    /**
     * Set the app's locale.
     * Updates the app's configuration with the new locale and applies the updated configuration.
     * If specified, restarts the activity for the locale change to take effect.
     * @param locale The desired locale.
     * @param onRecreate Whether the activity should be recreated after the locale change.
     */
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

    fun updateBottomNavigationBarTitles() {
        val navView: BottomNavigationView = binding.bottomNav
        navView.menu.findItem(R.id.menu_item1).title = getString(R.string.txt_create_command_menu)
        navView.menu.findItem(R.id.menu_item2).title = getString(R.string.txt_list_command_menu)
        navView.menu.findItem(R.id.menu_item3).title = getString(R.string.txt_settings_command_menu)
    }

}
