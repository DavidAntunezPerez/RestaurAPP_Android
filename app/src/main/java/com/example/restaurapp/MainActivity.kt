package com.example.restaurapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.restaurapp.databinding.ActivityMainBinding
import com.example.restaurapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // SIGN OUT BUTTON FUNCTION
        binding.btnSignOut.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        // SELECT TABLE OPTION
        binding.btnSelectTable.setOnClickListener {
            val intent = Intent(this, SelectTableActivity::class.java)
            startActivity(intent)
        }

        // COMMAND LIST OPTION
        binding.btnComList.setOnClickListener {
            val intent = Intent(this, ComListActivity::class.java)
            startActivity(intent)
        }


    }
}