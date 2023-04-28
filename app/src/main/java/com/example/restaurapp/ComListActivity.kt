package com.example.restaurapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.restaurapp.databinding.ActivityComListBinding
import com.example.restaurapp.databinding.ActivityMainBinding

class ComListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityComListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
    }
}