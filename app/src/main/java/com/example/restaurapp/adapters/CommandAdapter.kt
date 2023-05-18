package com.example.restaurapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurapp.R
import com.example.restaurapp.entities.Command
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class CommandAdapter(
    private val commandList: List<Command>,
    private val longClickListener: OnCommandLongClickListener
) : RecyclerView.Adapter<CommandAdapter.CommandViewHolder>() {

    interface OnCommandLongClickListener {
        fun onCommandLongClick(command: Command)
    }

    inner class CommandViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnLongClickListener {
        val tvTitle: TextView = itemView.findViewById(R.id.textCommandTitle)
        val tvDescr: TextView = itemView.findViewById(R.id.textCommandDescr)
        val tvTotalPrice: TextView = itemView.findViewById(R.id.textCommandTotalPrice)
        val tvTableAssigned: TextView = itemView.findViewById(R.id.textTableAssigned)

        init {
            itemView.setOnLongClickListener(this) // Set the long click listener
        }

        override fun onLongClick(view: View): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val command = commandList[position]
                longClickListener.onCommandLongClick(command)
                return true
            }
            return false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommandViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.command_list, parent, false)
        return CommandViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommandViewHolder, position: Int) {
        val command = commandList[position]
        holder.tvTitle.text = command.title
        holder.tvDescr.text = command.description

        // TRANSLATE THE TEXT DEPENDING ON THE LANGUAGE SET
        val totalPriceText = when (Locale.getDefault().language) {
            "es" -> "PRECIO TOTAL: $${command.totalPrice}"
            else -> "TOTAL PRICE: $${command.totalPrice}"
        }
        holder.tvTotalPrice.text = totalPriceText

        // Fetch the table document and retrieve the table number
        val firestore = FirebaseFirestore.getInstance()
        val tableRef = command.idTable?.let { firestore.collection("tables").document(it) }
        tableRef?.get()?.addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot?.exists() == true) {
                val tableNumber = documentSnapshot.getLong("number")
                val tableText = when (Locale.getDefault().language) {
                    "es" -> "MESA: ${tableNumber ?: "N/A"}"
                    else -> "TABLE: ${tableNumber ?: "N/A"}"
                }
                holder.tvTableAssigned.text = tableText
            } else {
                val tableText = when (Locale.getDefault().language) {
                    "es" -> "MESA: N/A"
                    else -> "TABLE: N/A"
                }
                holder.tvTableAssigned.text = tableText
            }
        }?.addOnFailureListener { exception ->
            val tableText = when (Locale.getDefault().language) {
                "es" -> "MESA: N/A"
                else -> "TABLE: N/A"
            }
            holder.tvTableAssigned.text = tableText
            Log.e("CommandAdapter", "Failed to fetch table document: $exception")
        }
    }


    override fun getItemCount(): Int {
        return commandList.size
    }
}