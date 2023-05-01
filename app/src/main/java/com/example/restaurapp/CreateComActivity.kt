package com.example.restaurapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.restaurapp.databinding.ActivityCreateComBinding

class CreateComActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateComBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCreateComBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // GETTING THE TABLE NUMBER INFORMATION
        val message = intent.getStringExtra("tableNumber")
        binding.txtNumberTable.text = message

    }
}