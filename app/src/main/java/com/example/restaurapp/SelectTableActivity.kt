package com.example.restaurapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.restaurapp.databinding.ActivityComListBinding
import com.example.restaurapp.databinding.ActivitySelectTableBinding

class SelectTableActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectTableBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
    }
}