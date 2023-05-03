package com.example.restaurapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.databinding.ActivityCreateComBinding
import com.example.restaurapp.firestore.Dish
import com.example.restaurapp.firestore.DishAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateComActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateComBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var dishList: ArrayList<Dish>
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCreateComBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // CREATE SHARED PREFERENCES ITEM AND RETRIEVING USER UID
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val userUID = sharedPreference.getString("userUID", "userUID")


        // BUTTON TO GO BACK TO PREVIOUS ACTIVITY
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // GETTING THE TABLE NUMBER INFORMATION
        val message = intent.getStringExtra("tableNumber")
        binding.txtNumberTable.text = message


        // SET UP THE RECYCLER VIEW
        recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(this)

        dishList = arrayListOf()

        db = FirebaseFirestore.getInstance()

        db.collection("dishes").get()
            .addOnSuccessListener {
                if (!it.isEmpty) {
                    for (data in it.documents) {
                        val dish: Dish? = data.toObject(Dish::class.java)
                        if ((dish != null) && (dish.idRestaurant == userUID)) {
                            dishList.add(dish)
                        }
                    }
                    recyclerView.adapter = DishAdapter(dishList)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }

    }
}