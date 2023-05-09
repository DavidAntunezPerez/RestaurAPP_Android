package com.example.restaurapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.databinding.ActivityCreateComBinding
import com.example.restaurapp.firestore.Dish
import com.example.restaurapp.firestore.DishAdapter
import com.example.restaurapp.fragments.CreateComFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateComActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateComBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var dishList: ArrayList<Dish>
    private lateinit var adapter: DishAdapter
    private lateinit var moreFragment: CreateComFragment
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

        // INITIALIZING FRAGMENT
        moreFragment = CreateComFragment()

        // HIDING THE MORE OPTIONS FRAGMENT IN THE ON CREATE
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentMoreCreateComInterface, moreFragment).hide(moreFragment).commit()

        Log.d("MoreFragmentHidden", "Fragment hidden: ${moreFragment.isHidden}")


        // BUTTON TO GO BACK TO PREVIOUS ACTIVITY
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // GETTING THE TABLE NUMBER INFORMATION AND DOCUMENT ID
        val tableNumber = intent.getStringExtra("tableNumber")
        binding.txtNumberTable.text = tableNumber

        val tableDocId = intent.getStringExtra("tableDocId")

        // BUTTON TO ADD MORE OPTIONS (LOAD FRAGMENT)
        binding.btnMore.setOnClickListener {
            val transaction = supportFragmentManager.beginTransaction()
            if (moreFragment.isHidden) {
                // Show the fragment
                transaction.show(moreFragment)

                // Set up the fragment to consume touch events when it's visible
                moreFragment.view?.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        // Call performClick() to simulate a click event
                        moreFragment.view?.performClick()
                        return@setOnTouchListener true
                    }
                    moreFragment.isVisible

                }
            } else {
                // Hide the fragment
                transaction.hide(moreFragment)
                findViewById<View>(R.id.fragmentMoreCreateComInterface)
                    .setOnTouchListener { _, _ -> false }
            }
            transaction.commit()
        }


        // SET UP THE RECYCLER VIEW--
        recyclerView = binding.recyclerview
        recyclerView.layoutManager = LinearLayoutManager(this)

        dishList = arrayListOf()

        db = FirebaseFirestore.getInstance()

        db.collection("dishes").get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                for (documentSnapshot in querySnapshot.documents) {
                    val dish: Dish? = documentSnapshot.toObject(Dish::class.java)
                    if ((dish != null) && (dish.idRestaurant == userUID)) {
                        dish.id = documentSnapshot.id // Set the document ID
                        dishList.add(dish)
                    }
                }
                recyclerView.adapter = DishAdapter(dishList)
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
        }

        // SET FUNCTION TO CREATE COM
        binding.btnCreateCom.setOnClickListener {
            saveComFirestore(userUID, tableDocId, it)
        }

    }

    private fun saveComFirestore(userUID: String?, idTable: String?, view: View) {
        val selectedDishes = (recyclerView.adapter as DishAdapter).getDishCreateList()
        val totalPrice = (recyclerView.adapter as DishAdapter).getTotalPrice()

        Log.i(
            "DISH LIST GET FROM ADAPTER",
            (recyclerView.adapter as DishAdapter).getDishCreateList().toString()
        )

        // CREATING A HASH MAP WITH THE INFORMATION NEEDED
        val command = hashMapOf(
            "title" to "Command",
            "idRestaurant" to userUID,
            "idTable" to idTable,
            "totalPrice" to totalPrice,
            "dishesList" to selectedDishes
        )

        Log.i("DISHCREATELIST TO STRING", selectedDishes.toString())


        if (selectedDishes.isNotEmpty()) {
            // Add a new document with a generated ID
            db.collection("commands").add(command).addOnSuccessListener { documentReference ->
                Snackbar.make(view, "Command created successfully", Snackbar.LENGTH_LONG).show()
                Log.d(
                    "Added command successfully",
                    "DocumentSnapshot added with ID: ${documentReference.id}"
                )
            }.addOnFailureListener { e ->
                Log.w("Error adding command", e)
                Snackbar.make(view, "ERROR, could not create the Command", Snackbar.LENGTH_LONG)
                    .show()
            }
        } else {
            // IF THE LIST OF DISHES IS EMPTY, YOU SHOULD NOT BE ABLE TO CREATE A COMMAND
            Snackbar.make(view, "You cannot create an empty Command!", Snackbar.LENGTH_LONG).show()
        }

        // DELETE TOTAL PRICE WHEN PROCESS ENDED
        (recyclerView.adapter as DishAdapter).clearTotalPrice()

        // DELETE THE ACTUAL LIST WHEN PROCESS ENDED
        (recyclerView.adapter as DishAdapter).deleteAllDishCreateList()

    }

}