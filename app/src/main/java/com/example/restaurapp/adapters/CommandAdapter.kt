package com.example.restaurapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.example.restaurapp.entities.Command

class CommandAdapter(private val commandList: ArrayList<Command>) :
    RecyclerView.Adapter<CommandAdapter.CommandViewHolder>() {

    class CommandViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.textCommandTitle)
        val tvDescr: TextView = itemView.findViewById(R.id.textCommandDescr)
        val tvTotalPrice: TextView = itemView.findViewById(R.id.textCommandTotalPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommandViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.command_list, parent, false)

        return CommandViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: CommandViewHolder, position: Int) {

        holder.tvTitle.text = commandList[position].title
        holder.tvDescr.text = commandList[position].description
        holder.tvTotalPrice.text = commandList[position].totalPrice.toString()

    }

    override fun getItemCount(): Int {
        return commandList.size
    }


}