package com.example.restaurapp.firestore

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.File

class DishAdapter(private val dishList: ArrayList<Dish>) :
    RecyclerView.Adapter<DishAdapter.DishViewHolder>() {

    private var dishCreateList = mutableListOf<Dish>()

    class DishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.textDishName)
        val tvPrice: TextView = itemView.findViewById(R.id.txtDishPrize)
        val ivImage: ImageView = itemView.findViewById(R.id.imgDish)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.dishes_list, parent, false)

        return DishViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DishViewHolder, position: Int) {
        holder.tvName.text = dishList[position].name
        holder.tvPrice.text = dishList[position].price.toString()

        // LOAD IMAGES
        // REFERENCING FIREBASE STORAGE
        var storageRef = Firebase.storage.reference

        // REMOVING THE FAKE PATH IN CASE OF EXISTING IN ITEM FIELD
        val cleanedPath =
            "/images/" + pathClean(dishList[position].image.toString(), "C:\\fakepath\\")
        Log.i("cleanedPathVAL", cleanedPath)

        // REFERENCING THE RESULTING PATH INTO A CHILD
        val pathReference = storageRef.child(cleanedPath)

        // GENERATING AN EMPTY LOCAL FILE  AND MAKING IT RETRIEVE THE DOWNLOADED IMAGE FROM STORAGE
        val localFile = File.createTempFile("images", ".jpg")
        pathReference.getFile(localFile).addOnSuccessListener {
            Picasso.get().load(localFile).into(holder.ivImage)
        }.addOnFailureListener {
            Log.e("IMG STORAGE ERROR", "Error getting the temp file")
        }

        holder.itemView.setOnClickListener {
            // RETRIEVE ITEM DATA
            val selectedId = dishList[position].id
            val selectedIdRestaurant = dishList[position].idRestaurant
            val selectedName = dishList[position].name
            val selectedPrice = dishList[position].price
            val selectedImage = dishList[position].image
            // CREATING OBJECT DISH AND ADDING IT TO THE LIST
            val dish =
                Dish(selectedId, selectedIdRestaurant, selectedName, selectedPrice, selectedImage)
            addDishCreateList(dish)
            Snackbar.make(it, "Dish added to the Command", Snackbar.LENGTH_SHORT).show()
            Log.i("DISHCREATE LIST CHECK", dishCreateList.toString())
        }

    }

    override fun getItemCount(): Int {
        return dishList.size
    }

    // FUNCTION TO REMOVE THE FAKE LOCATION CREATED BY DEFAULT IN WEB APPLICATION
    private fun pathClean(text: String, remove: String): String {
        return (text.replace(remove, ""))
    }

    fun addDishCreateList(dish: Dish) {
        dishCreateList.add(dish)
    }

    fun deleteDishCreateList(dish: Dish) {
        dishCreateList.remove(dish)
    }

    fun deleteAllDishCreateList() {
        dishCreateList.clear()
    }

    fun getDishCreateList(): List<Dish> {
        return dishCreateList.toList()
    }
}