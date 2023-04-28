package com.example.restaurapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.databinding.ActivityComListBinding
import com.example.restaurapp.databinding.ActivitySelectTableBinding
import com.example.restaurapp.firestore.SelectTableAdapter
import com.example.restaurapp.firestore.Table
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app

class SelectTableActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectTableBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var tableList: ArrayList<Table>
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // GO BACK BUTTON FUNCTION
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // SETTING UP THE RECYCLERVIEW
        recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(this)

        tableList = arrayListOf()

        db = FirebaseFirestore.getInstance()

        db.collection("tables").get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val table: Table? = data.toObject(Table::class.java)
                        if (table != null) {
                            tableList.add(table)
                        }
                    }
                    recyclerView.adapter = SelectTableAdapter(tableList)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }


    }
}