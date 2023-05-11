package com.example.restaurapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.example.restaurapp.entities.Dish

class DishCCMoreAdapter(private val dishesList: List<Dish>) :
    RecyclerView.Adapter<DishCCMoreAdapter.DishCCMoreViewHolder>() {
    class DishCCMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDishName: TextView = itemView.findViewById(R.id.textCCMoreDishName)
        val tvDishPrice: TextView = itemView.findViewById(R.id.txtCCMoreDishPrize)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishCCMoreViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dishesmore_list, parent, false)
        return DishCCMoreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DishCCMoreViewHolder, position: Int) {
        holder.tvDishName.text = dishesList[position].name
        holder.tvDishPrice.text = dishesList[position].price.toString()
    }

    override fun getItemCount(): Int {
        return dishesList.size
    }

}