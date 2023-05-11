package com.example.restaurapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.example.restaurapp.activities.CreateComActivity
import com.example.restaurapp.entities.Dish
import com.google.android.material.button.MaterialButton

class DishCCMoreAdapter(private val dishesList: MutableList<Dish>) :
    RecyclerView.Adapter<DishCCMoreAdapter.DishCCMoreViewHolder>() {
    class DishCCMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDishName: TextView = itemView.findViewById(R.id.textCCMoreDishName)
        val tvDishPrice: TextView = itemView.findViewById(R.id.txtCCMoreDishPrize)
        val mbDeleteButton: MaterialButton = itemView.findViewById(R.id.btnCCMoreDelete)
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

        holder.mbDeleteButton.setOnClickListener {
            Log.i("TDL DISH CLICKED", "${dishesList[position]} VIEW:${it}")
            removeSelectedDish(dishesList[position], it)
        }

    }

    override fun getItemCount(): Int {
        return dishesList.size
    }

    fun removeSelectedDish(dish: Dish, view: View) {
        dishesList.remove(dish)
        Log.i("TDL DISH REMOVED", "$dishesList")
        notifyDataSetChanged()
        (view.context as CreateComActivity).adapter.replaceDishCreateList(dishesList)
    }

    fun getCCMoreList(): MutableList<Dish> {
        return dishesList
    }

}