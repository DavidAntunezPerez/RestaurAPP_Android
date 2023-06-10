package com.example.restaurapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.activities.CreateComActivity
import com.example.restaurapp.R
import com.example.restaurapp.entities.Table


/**
 * Adapter class for the RecyclerView in the SelectTableActivity.
 * @param tableList The list of tables to display.
 */
class SelectTableAdapter(private val tableList: ArrayList<Table>) :
    RecyclerView.Adapter<SelectTableAdapter.SelectTableViewHolder>() {

    /** ViewHolder for the table item */
    class SelectTableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumber: TextView = itemView.findViewById(R.id.textNumber)
        val tvInfo: TextView = itemView.findViewById(R.id.textInfo)
    }

    /**
     * Creates a new SelectTableViewHolder instance.
     * @param parent The parent ViewGroup.
     * @param viewType The view type integer.
     * @return The created SelectTableViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectTableViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.table_list, parent, false)

        return SelectTableViewHolder(itemView)
    }

    /**
     * Binds the data to the SelectTableViewHolder.
     * @param holder The SelectTableViewHolder to bind the data to.
     * @param position The position of the item in the tableList.
     */
    override fun onBindViewHolder(holder: SelectTableViewHolder, position: Int) {
        holder.tvNumber.text = tableList[position].number.toString()
        holder.tvInfo.text = tableList[position].info

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, CreateComActivity::class.java)
            // WE WILL SEND THE TABLE NUMBER INFORMATION TO CREATE COMMANDS
            intent.putExtra("tableNumber", holder.tvNumber.text)
            intent.putExtra("tableDocId", tableList[position].documentId)
            holder.itemView.context.startActivity(intent)
        }
    }

    /**
     * Returns the number of items in the tableList.
     * @return The item count.
     */
    override fun getItemCount(): Int {
        return tableList.size
    }
}
