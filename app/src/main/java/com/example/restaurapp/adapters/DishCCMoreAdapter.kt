package com.example.restaurapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.example.restaurapp.entities.Dish
import com.google.android.material.button.MaterialButton

class DishCCMoreAdapter(
    private val dishesList: MutableList<Dish>,
    var dishRemovedListener: DishRemovedListener? = null
) : RecyclerView.Adapter<DishCCMoreAdapter.DishCCMoreViewHolder>() {

    private var priceDishesRemoved: Double = 0.0

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
            val selectedPrice = dishesList[position].price

            Log.i("TDL DISH CLICKED", "${dishesList[position]} VIEW:${it}")


            // ADD DISH PRICE TO THE PRICE OF DISHES REMOVED
            if (selectedPrice != null) {
                priceDishesRemoved = priceDishesRemoved.plus(selectedPrice)
                Log.i("TPD PRICE DISHES REMOVED", "$priceDishesRemoved")
            }

            Log.i("TPD PRICE DISHES ADAPTER CC", "$selectedPrice , $priceDishesRemoved")

            // REMOVE DISH IN THE LIST
            dishesList.remove(dishesList[position])
            Log.i("TDL DISH REMOVED", "$dishesList")
            notifyDataSetChanged()

            // WE CALL THE INTERFACE TO SET THE NEW PRICE
            dishRemovedListener?.onDishRemovedUpdatePrice(priceDishesRemoved)
            // ONCE SENT THE PRICE REMOVED, WE CLEAR THE VARIABLE SO IT DOESN'T STACK
            priceDishesRemoved = 0.0

        }

    }

    override fun getItemCount(): Int {
        return dishesList.size
    }

    fun getCCMoreList(): MutableList<Dish> {
        return dishesList
    }

    fun clearCCMoreList() {
        dishesList.clear()
    }

    // INTERFACE TO UPDATE PRICE OF DISH REMOVED
    interface DishRemovedListener {
        fun onDishRemovedUpdatePrice(dishRemovedPrice: Double)
    }

}