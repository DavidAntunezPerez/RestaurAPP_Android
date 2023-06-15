package com.example.restaurapp.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View

import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.example.restaurapp.databinding.ActivityCreateComBinding
import com.example.restaurapp.entities.Dish
import com.example.restaurapp.adapters.DishAdapter
import com.example.restaurapp.adapters.DishCCMoreAdapter
import com.example.restaurapp.fragments.CreateComFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

/**
 *  Activity made for managing the creation of Commands
 **/
class CreateComActivity : AppCompatActivity(), CreateComFragment.OnDataPass,
    DishCCMoreAdapter.DishRemovedListener {
    private lateinit var binding: ActivityCreateComBinding
    private lateinit var dishRecyclerView: RecyclerView
    private lateinit var dishList: ArrayList<Dish>
    private lateinit var adapter: DishAdapter
    private lateinit var moreFragment: CreateComFragment
    private lateinit var searchView: SearchView
    private var db = Firebase.firestore

    // TITLE AND DESCRIPTION RETRIEVED IN THE FRAGMENT INPUT
    private var title: String? = null
    private var description: String? = null

    /**
     * Initializes the activity and sets up the views.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateComBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // CHANGE THE STATUS BAR COLOR
        window.statusBarColor = ContextCompat.getColor(this, R.color.app_secondary_pink)

        // INITIALIZING DISH LIST
        dishList = ArrayList()

        // INITIALIZING THE ADAPTER
        adapter = DishAdapter(dishList, this)

        // INITIALIZE SEARCHVIEW
        searchView = findViewById(R.id.searchView)

        // CREATE SHARED PREFERENCES ITEM AND RETRIEVING USER UID
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val userUID = sharedPreference.getString("userUID", "userUID")

        // INITIALIZING FRAGMENT
        moreFragment = CreateComFragment()
        moreFragment.dishRemovedListener = this // Set the listener to the activity

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

                moreFragment.setDishList((dishRecyclerView.adapter as DishAdapter).getDishCreateList())
                // Show the fragment
                transaction.show(moreFragment)
                moreFragment.rvLoadAnimation()

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

                (dishRecyclerView.adapter as DishAdapter).replaceDishCreateList(moreFragment.hideFragment())
                // Hide the fragment
                transaction.hide(moreFragment)
                findViewById<View>(R.id.fragmentMoreCreateComInterface).setOnTouchListener { _, _ -> false }

                // ADD ANIMATION WHEN LOADING ALL THE RV
                dishRecyclerView.startLayoutAnimation()
            }

            transaction.commit()
        }

        // SET UP THE RECYCLER VIEW
        dishRecyclerView = binding.recyclerview
        dishRecyclerView.layoutManager = LinearLayoutManager(this)

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
                dishRecyclerView.adapter = DishAdapter(dishList, this)

                // ADD ANIMATION WHEN LOADING ALL THE RV
                dishRecyclerView.startLayoutAnimation()

            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()
        }

        // SET FUNCTION TO CREATE COM
        binding.btnCreateCom.setOnClickListener {
            saveComFirestore(userUID, tableDocId, title, description, it)
        }

        // SEARCH FUNCTION
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })

    }

    /**
     * Function that filter the list of dishes depending on the user input
     *
     * @param query Text inputted from the user
     */
    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<Dish>()
            val lowerCaseQuery = query.lowercase(Locale.ROOT)
            for (dish in dishList) {
                val lowerCaseName = dish.name?.lowercase(Locale.ROOT)
                val upperCaseName = dish.name?.uppercase(Locale.ROOT)

                if (lowerCaseName?.contains(lowerCaseQuery) == true || upperCaseName?.contains(
                        lowerCaseQuery
                    ) == true
                ) {
                    filteredList.add(dish)
                }
            }

            if (filteredList.isEmpty()) {
                Snackbar.make(
                    binding.root, getString(R.string.notfoundfilter), Snackbar.LENGTH_SHORT
                ).show()
            }

            adapter.setFilteredList(filteredList)
            dishRecyclerView.adapter = adapter
        }
    }


    /**
     * Receives the data passed from the fragment and stores the title and description.
     *
     * @param title The title passed from the fragment.
     * @param description The description passed from the fragment.
     */
    override fun onDataPass(title: String, description: String) {
        this.title = title
        this.description = description
    }

    /**
     * Saves the command to Firestore.
     *
     * @param userUID The UID of the user.
     * @param idTable The ID of the table.
     * @param title The title of the command.
     * @param description The description of the command.
     * @param view The view used to display a Snackbar.
     */
    private fun saveComFirestore(
        userUID: String?, idTable: String?, title: String?, description: String?, view: View
    ) {
        val selectedDishes = (dishRecyclerView.adapter as DishAdapter).getDishCreateList()
        val totalPrice = (dishRecyclerView.adapter as DishAdapter).getTotalPrice()

        // CREATING A HASH MAP WITH THE INFORMATION NEEDED
        val command = hashMapOf(
            "title" to title,
            "description" to description,
            "idRestaurant" to userUID,
            "idTable" to idTable,
            "totalPrice" to totalPrice,
            "dishesList" to selectedDishes
        )

        Log.i("DISHCREATELIST TO STRING", selectedDishes.toString())

        if (selectedDishes.isNotEmpty()) {
            // Add a new document with a generated ID
            db.collection("commands").add(command).addOnSuccessListener { documentReference ->
                Snackbar.make(
                    view, getString(R.string.success_create_command), Snackbar.LENGTH_LONG
                ).show()
                Log.d(
                    "Added command successfully",
                    "DocumentSnapshot added with ID: ${documentReference.id}"
                )
            }.addOnFailureListener { e ->
                Log.w("Error adding command", e)
                Snackbar.make(view, getString(R.string.error_create_command), Snackbar.LENGTH_LONG)
                    .show()
            }
        } else {
            // IF THE LIST OF DISHES IS EMPTY, YOU SHOULD NOT BE ABLE TO CREATE A COMMAND
            Snackbar.make(view, getString(R.string.cannot_create_command), Snackbar.LENGTH_LONG)
                .show()
        }

        // DELETE TOTAL PRICE WHEN PROCESS ENDED
        (dishRecyclerView.adapter as DishAdapter).clearTotalPrice()

        // DELETE THE ACTUAL LIST WHEN PROCESS ENDED
        (dishRecyclerView.adapter as DishAdapter).deleteAllDishCreateList()

        // CLEAR FRAGMENT ADAPTER
        moreFragment.clearCCMoreAdapter()
    }

    /**
     * Updates the total price when a dish is removed.
     *
     * @param dishRemovedPrice The price of the removed dish.
     */
    override fun onDishRemovedUpdatePrice(dishRemovedPrice: Double) {
        (dishRecyclerView.adapter as DishAdapter).removeDishPrice(dishRemovedPrice)
        Log.i("TPD ONDISHREMOVED ACTIVITY", "$dishRemovedPrice")
    }
}