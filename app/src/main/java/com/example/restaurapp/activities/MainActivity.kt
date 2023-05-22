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


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // GET USER UID
        val userUID = firebaseAuth.currentUser?.uid

        // STORE USER UID IN SHARED PREFERENCES
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        // PUTTING IT INTO A STRING
        editor.putString("userUID", userUID)
        editor.apply()

//        // SIGN OUT BUTTON FUNCTION
//        binding.btnSignOut.setOnClickListener {
//            firebaseAuth.signOut()
//            startActivity(Intent(this, SignInActivity::class.java))
//            finish()
//        }

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
}