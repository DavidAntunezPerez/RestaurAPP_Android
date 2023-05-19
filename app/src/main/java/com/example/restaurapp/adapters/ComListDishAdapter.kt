package com.example.restaurapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.example.restaurapp.entities.Dish

class ComListDishAdapter(private val dishes: List<Dish>) :
    RecyclerView.Adapter<ComListDishAdapter.ComListDishViewHolder>() {

    class ComListDishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDishName: TextView = itemView.findViewById(R.id.textComListDishName)
        val tvDishPrice: TextView = itemView.findViewById(R.id.txtComListDishPrize)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ComListDishViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.comlist_dishes_list, parent, false)

        return ComListDishViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ComListDishViewHolder, position: Int) {
        val dish = dishes[position]
        holder.tvDishName.text = dish.name
        holder.tvDishPrice.text = "$" + dish.price.toString()
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

}