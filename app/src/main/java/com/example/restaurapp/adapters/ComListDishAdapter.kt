package com.example.restaurapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.example.restaurapp.entities.Dish

/**
 * Adapter made for managing the List of Dishes
 * @param dishes The list of dishes to display
 **/
class ComListDishAdapter(private val dishes: List<Dish>) :
    RecyclerView.Adapter<ComListDishAdapter.ComListDishViewHolder>() {

    /**
     * ViewHolder class for displaying individual dishes in the RecyclerView.
     */
    class ComListDishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDishName: TextView = itemView.findViewById(R.id.textComListDishName)
        val tvDishPrice: TextView = itemView.findViewById(R.id.txtComListDishPrize)
    }

    /**
     * Creates a new ComListDishViewHolder instance.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ComListDishViewHolder instance.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComListDishViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.comlist_dishes_list, parent, false)

        return ComListDishViewHolder(itemView)
    }

    /**
     * Binds the data to the ComListDishViewHolder.
     *
     * @param holder The ComListDishViewHolder to bind the data to.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ComListDishViewHolder, position: Int) {
        val dish = dishes[position]
        holder.tvDishName.text = dish.name
        holder.tvDishPrice.text = "$" + dish.price.toString()
    }

    /**
     * Returns the total number of dishes in the data set held by the adapter.
     *
     * @return The total number of dishes.
     */
    override fun getItemCount(): Int {
        return dishes.size
    }

}
