package com.example.restaurapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.restaurapp.databinding.ActivityCreateComBinding

class CreateComActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateComBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCreateComBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // CREATE SHARED PREFERENCES ITEM AND RETRIEVING USER UID
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val userUID = sharedPreference.getString("userUID", "userUID")

        // PUTTING USER ID INTO TXT FOR TESTING
        binding.txtCreateComFor.text = userUID

        // BUTTON TO GO BACK TO PREVIOUS ACTIVITY
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // GETTING THE TABLE NUMBER INFORMATION
        val message = intent.getStringExtra("tableNumber")
        binding.txtNumberTable.text = message

    }
}