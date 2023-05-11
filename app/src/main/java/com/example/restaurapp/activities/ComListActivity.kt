package com.example.restaurapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.restaurapp.databinding.ActivityComListBinding
class ComListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityComListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComListBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}