package com.example.restaurapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.databinding.ActivityCreateComBinding
import com.example.restaurapp.firestore.Dish
import com.example.restaurapp.firestore.DishAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.log

class CreateComActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateComBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var dishList: ArrayList<Dish>
    private lateinit var adapter: DishAdapter
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCreateComBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // INITIALIZING DISH LIST
        dishList = ArrayList()

        // INITIALIZING THE ADAPTER
        adapter = DishAdapter(dishList)

        // CREATE SHARED PREFERENCES ITEM AND RETRIEVING USER UID
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val userUID = sharedPreference.getString("userUID", "userUID")


        // BUTTON TO GO BACK TO PREVIOUS ACTIVITY
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // GETTING THE TABLE NUMBER INFORMATION
        val tableNumber = intent.getStringExtra("tableNumber")
        binding.txtNumberTable.text = tableNumber


        // SET UP THE RECYCLER VIEW
        recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(this)

        dishList = arrayListOf()

        db = FirebaseFirestore.getInstance()

        db.collection("dishes").get().addOnSuccessListener {
            if (!it.isEmpty) {
                for (data in it.documents) {
                    val dish: Dish? = data.toObject(Dish::class.java)
                    if ((dish != null) && (dish.idRestaurant == userUID)) {
                        dishList.add(dish)
                    }
                }
                recyclerView.adapter = DishAdapter(dishList)
            }
        }.addOnFailureListener {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }

        // SET FUNCTION TO CREATE COM
        binding.btnCreateCom.setOnClickListener {
            saveComFirestore(userUID, tableNumber, it)
        }

    }

    private fun saveComFirestore(userUID: String?, idTable: String?, view: View) {
        val selectedDishes = (recyclerView.adapter as DishAdapter).getDishCreateList()

        // TODO: MAKE FUNCTIONAL TOTALPRICE, RETRIEVE TABLE COLLECTION ID AND NOT NUMBER ID,
        //  ADD SNACKBARS WHEN CLICKING EACH OF THE ITEMS

        // TEST TO SEE IF YOU CAN CREATE A COLLECTION WITH ANY INFORMATION
        val command = hashMapOf(
            "title" to "Command",
            "idRestaurant" to userUID,
            "idTable" to idTable,
            "totalPrice" to 0,
            "dishesList" to selectedDishes
        )

        Log.i("DISHCREATELIST TO STRING", selectedDishes.toString())

        // Add a new document with a generated ID
        db.collection("commands").add(command).addOnSuccessListener { documentReference ->
            Snackbar.make(view, "Command created successfully", Snackbar.LENGTH_LONG).show()
            Log.d(
                "Added command successfully",
                "DocumentSnapshot added with ID: ${documentReference.id}"
            )
        }.addOnFailureListener { e ->
            Log.w("Error adding command", e)
            Snackbar.make(view, "ERROR, could not create the Command", Snackbar.LENGTH_LONG).show()
        }

    }
}