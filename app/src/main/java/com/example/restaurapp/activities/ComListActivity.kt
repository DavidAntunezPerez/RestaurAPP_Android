package com.example.restaurapp.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.example.restaurapp.adapters.CommandAdapter
import com.example.restaurapp.databinding.ActivityComListBinding
import com.example.restaurapp.entities.Command
import com.example.restaurapp.entities.Dish
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class ComListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityComListBinding
    private lateinit var commandRecyclerView: RecyclerView
    private lateinit var commandList: ArrayList<Command>
    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // GET SHARED PREFERENCES ITEM
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val userUID = sharedPreference.getString("userUID", "userUID")

        // GET THE LANGUAGE SETTED
        val language = Locale.getDefault().language

        // GO BACK BUTTON FUNCTION
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // LOADING ANIMATION
        val loadingImageView: ImageView = binding.loadingAnimation

        // SETTING UP THE RECYCLERVIEW
        commandRecyclerView = binding.commandrecyclerview
        commandRecyclerView.layoutManager = LinearLayoutManager(this)

        commandList = arrayListOf()

        val animation = AnimationUtils.loadAnimation(this, R.anim.loading_anim)
        loadingImageView.startAnimation(animation)

        db.collection("commands").get().addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                for (documentSnapshot in querySnapshot.documents) {
                    val command: Command? = documentSnapshot.toObject(Command::class.java)
                    if ((command != null) && (command.idRestaurant == userUID)) {
                        command.id = documentSnapshot.id // Set the document ID

                        // Retrieve the dishesList property as a List<Map<String, Any>>
                        val dishesList =
                            documentSnapshot.get("dishesList") as? List<Map<String, Any>>

                        // Convert each dish map to a Dish object
                        val dishList = dishesList?.map { dishMap ->
                            Dish(
                                id = dishMap["id"] as? String,
                                idRestaurant = dishMap["idRestaurant"] as? String,
                                image = dishMap["image"] as? String,
                                name = dishMap["name"] as? String,
                                price = dishMap["price"] as? Double ?: 0.0
                            )
                        }

                        // Set the dishList in the command object
                        command.dishesList = dishList

                        // Check if the title is null and set it based on the language
                        when (language) {
                            "es" -> {
                                if (command.title == null) {
                                    command.title = "Comando sin Título"
                                }
                            }

                            else -> {
                                if (command.title == null) {
                                    command.title = "Untitled Command"
                                }
                            }
                        }

                        // Check if the description is null and set it based on the language
                        when (language) {
                            "es" -> {
                                if (command.description == null) {
                                    command.description = "Sin descripción"
                                }
                            }

                            else -> {
                                if (command.description == null) {
                                    command.description = "No description provided"
                                }
                            }
                        }

                        commandList.add(command)
                    }
                }
                commandRecyclerView.adapter = CommandAdapter(commandList)

                // ADD ANIMATION WHEN LOADING ALL THE RV
                commandRecyclerView.startLayoutAnimation()

                // HIDE LOADING ANIMATION
                stopAnimation(loadingImageView)
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, exception.toString(), Toast.LENGTH_SHORT).show()

            // HIDE LOADING ANIMATION
            stopAnimation(loadingImageView)
        }

    }

    // FUNCTION TO STOP THE ANIMATION WHEN THE RV LOADS
    private fun stopAnimation(imageView: ImageView) {
        imageView.clearAnimation()
        imageView.visibility = View.GONE
    }

}