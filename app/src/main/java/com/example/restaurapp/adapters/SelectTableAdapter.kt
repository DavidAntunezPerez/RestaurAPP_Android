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

class SelectTableAdapter(private val tableList: ArrayList<Table>) :
    RecyclerView.Adapter<SelectTableAdapter.SelectTableViewHolder>() {
    class SelectTableViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumber: TextView = itemView.findViewById(R.id.textNumber)
        val tvInfo: TextView = itemView.findViewById(R.id.textInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectTableViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.table_list, parent, false)

        return SelectTableViewHolder(itemView)
    }

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

    override fun getItemCount(): Int {
        return tableList.size
    }


}