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

/**
 * Adapter for the RecyclerView used to display a list of dishes in the DishCCMoreFragment.
 *
 * @param dishesList The list of dishes to display.
 * @param dishRemovedListener Listener to handle dish removal and update the price.
 */
class DishCCMoreAdapter(
    private val dishesList: MutableList<Dish>, var dishRemovedListener: DishRemovedListener? = null
) : RecyclerView.Adapter<DishCCMoreAdapter.DishCCMoreViewHolder>() {

    private var priceDishesRemoved: Double = 0.0

    /**
     * ViewHolder class to hold the views for a single item in the RecyclerView.
     *
     * @param itemView The item view for the dish.
     */
    class DishCCMoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDishName: TextView = itemView.findViewById(R.id.textCCMoreDishName)
        val tvDishPrice: TextView = itemView.findViewById(R.id.txtCCMoreDishPrize)
        val mbDeleteButton: MaterialButton = itemView.findViewById(R.id.btnCCMoreDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishCCMoreViewHolder {
        // Inflate the item view layout
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.dishesmore_list, parent, false)
        return DishCCMoreViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DishCCMoreViewHolder, position: Int) {
        // Bind the data to the views
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

    /**
     * Returns the list of dishes displayed in the adapter.
     *
     * @return The list of dishes.
     */
    fun getCCMoreList(): MutableList<Dish> {
        return dishesList
    }

    /**
     * Clears the list of dishes displayed in the adapter.
     */
    fun clearCCMoreList() {
        dishesList.clear()
    }

    // INTERFACE TO UPDATE PRICE OF DISH REMOVED

    /**
     * Listener interface to handle dish removal and update the price.
     */
    interface DishRemovedListener {
        /**
         * Callback method called when a dish is removed, providing the updated dish removed price.
         *
         * @param dishRemovedPrice The updated price of the removed dish.
         */
        fun onDishRemovedUpdatePrice(dishRemovedPrice: Double)
    }
}
