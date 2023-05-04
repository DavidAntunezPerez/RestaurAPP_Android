package com.example.restaurapp.firestore

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import java.io.File

class DishAdapter(private val dishList: ArrayList<Dish>) :
    RecyclerView.Adapter<DishAdapter.DishViewHolder>() {

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
        var storageRef = Firebase.storage.reference
        val pathReference = storageRef.child(dishList[position].image.toString())
        val localFile = File.createTempFile("images", ".jpg")
        pathReference.getFile(localFile).addOnSuccessListener {
            Picasso.get().load(localFile).into(holder.ivImage)
        }.addOnFailureListener {
            Log.e("IMG STORAGE ERROR", "Error getting the temp file")
        }
    }

    override fun getItemCount(): Int {
        return dishList.size
    }


}