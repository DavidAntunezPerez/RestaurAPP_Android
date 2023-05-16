package com.example.restaurapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.restaurapp.R
import com.example.restaurapp.entities.Dish
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File

class DishAdapter(private val dishList: ArrayList<Dish>, private val context: Context) :
    RecyclerView.Adapter<DishAdapter.DishViewHolder>() {

    // MUTABLE LIST CONTAINING ALL THE DISHES ADDED IN A COMMAND
    private var dishCreateList = mutableListOf<Dish>()

    // VARIABLE CONTAINING THE TOTAL PRICE OF THE COMMAND
    private var totalPrice: Double = 0.0

    // Reference to the total price TextView
    private var totalPriceTextView: TextView? = null

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
        val storageRef = Firebase.storage.reference

        // REMOVING THE FAKE PATH IN CASE OF EXISTING IN ITEM FIELD
        val cleanedPath =
            "/images/" + pathClean(dishList[position].image.toString(), "C:\\fakepath\\")
        Log.i("cleanedPathVAL", cleanedPath)

        // REFERENCING THE RESULTING PATH INTO A CHILD
        val pathReference = storageRef.child(cleanedPath)

        // SET THE MAX SIZE OF THE IMAGE (5MB IN THIS CASE)
        val fileMaxSize: Long = 5 * 1000000

        pathReference.getBytes(fileMaxSize).addOnSuccessListener { path ->

            // LOADING THE IMAGE WITH GLIDE USING THE PATH GIVEN
            Glide.with(context)
                .load(path)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(400, 400)
                .centerCrop()
                .error(R.drawable.dish_error_img) // Error image if loading fails
                .into(holder.ivImage)

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

            // ADDING DISH PRICE TO TOTAL PRICE
            if (selectedPrice != null) {
                totalPrice = totalPrice.plus(selectedPrice)
            }

            // UPDATE THE TOTAL PRICE
            val txtTotalPrice: TextView =
                (it.context as AppCompatActivity).findViewById(R.id.txtTotalPrize)
            totalPriceTextView = txtTotalPrice
            txtTotalPrice.text = "$totalPrice"

            // CREATING A SNACK BAR TO INFORM ITEM WAS ADDED
            Snackbar.make(it, "Dish added to the Command", Snackbar.LENGTH_SHORT).show()
            Log.i("DISHCREATE LIST CHECK", dishCreateList.toString())
            Log.i("TPD TOTAL PRICE", "$totalPrice")
        }

    }

    override fun getItemCount(): Int {
        return dishList.size
    }

    // FUNCTION TO REMOVE THE FAKE LOCATION CREATED BY DEFAULT IN WEB APPLICATION
    private fun pathClean(text: String, remove: String): String {
        return (text.replace(remove, ""))
    }

    // DISH CREATE LIST FUNCTIONS
    fun addDishCreateList(dish: Dish) {
        dishCreateList.add(dish)
    }

    fun deleteDishCreateList(dish: Dish) {
        dishCreateList.remove(dish)
    }

    fun replaceDishCreateList(list: MutableList<Dish>) {
        dishCreateList = list
        Log.i("TDL DISH REPLACED", "$dishCreateList")
        Log.i("TDL DISH REPLACED SIZE", "${dishCreateList.size}")
        notifyDataSetChanged()
    }

    fun removeDishPrice(dishRemovedPrice: Double) {
        Log.i("TPD TOTALPRICE onRemoveDish", "$totalPrice, $dishRemovedPrice")
        // WE SUBTRACT THE PRICE OF THE DISH ELIMINATED IN THE TOTAL PRICE
        totalPrice -= dishRemovedPrice
        Log.i("TPD TOTALPRICE AFTER REMOVEDISHPRICE", "$totalPrice")
        // SET THE VALUE IN SCREEN
        totalPriceTextView?.text = totalPrice.toString()
        notifyDataSetChanged()
    }

    fun deleteAllDishCreateList() {
        dishCreateList.clear()
    }

    fun getDishCreateList(): List<Dish> {
        return dishCreateList.toList()
    }

    // TOTAL PRICE FUNCTIONS
    fun getTotalPrice(): Double {
        return totalPrice
    }

    fun clearTotalPrice() {
        totalPrice = 0.0
        totalPriceTextView?.text = "0" // Set the value to 0
    }

}