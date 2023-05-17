package com.example.restaurapp.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.databinding.ActivitySelectTableBinding
import com.example.restaurapp.adapters.SelectTableAdapter
import com.example.restaurapp.entities.Table
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SelectTableActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectTableBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var tableList: ArrayList<Table>
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // CREATE SHARED PREFERENCES ITEM
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val userUID = sharedPreference.getString("userUID", "userUID")

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
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (documentSnapshot in querySnapshot.documents) {
                        val table: Table? = documentSnapshot.toObject(Table::class.java)
                        if ((table != null) && (table.idRestaurant == userUID)) {
                            table.documentId = documentSnapshot.id // Set the document ID
                            tableList.add(table)
                        }
                    }
                    recyclerView.adapter = SelectTableAdapter(tableList)

                    // ADD ANIMATION WHEN LOADING ALL THE RV
                    recyclerView.startLayoutAnimation()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
            }
    }
}