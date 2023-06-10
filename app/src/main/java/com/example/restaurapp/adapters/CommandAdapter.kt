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


/**
 * Adapter class for the Command list RecyclerView.
 *
 * @param commandList The list of Command objects to display.
 * @param longClickListener The listener for long click events.
 * @param itemClickListener The listener for item click events.
 */
class CommandAdapter(
    private val commandList: List<Command>,
    private val longClickListener: OnCommandLongClickListener,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<CommandAdapter.CommandViewHolder>() {

    /**
     * Interface for item click events.
     */
    interface OnItemClickListener {
        /**
         * Called when an item is clicked.
         *
         * @param command The clicked command.
         */
        fun onItemClick(command: Command)
    }

    /**
     * Interface for long click events.
     */
    interface OnCommandLongClickListener {
        /**
         * Called when an item is long clicked.
         *
         * @param command The long clicked command.
         */
        fun onCommandLongClick(command: Command)
    }

    /**
     * ViewHolder for the command items.
     *
     * @param itemView The view for the item.
     */
    inner class CommandViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnLongClickListener {
        val tvTitle: TextView = itemView.findViewById(R.id.textCommandTitle)
        val tvDescr: TextView = itemView.findViewById(R.id.textCommandDescr)
        val tvTotalPrice: TextView = itemView.findViewById(R.id.textCommandTotalPrice)
        val tvTableAssigned: TextView = itemView.findViewById(R.id.textTableAssigned)

        init {
            itemView.setOnLongClickListener(this) // Set the long click listener
        }

        /**
         * Called when the item is long clicked.
         *
         * @param view The view that was clicked.
         * @return True if the long click is consumed, false otherwise.
         */
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

    /**
     * Creates a new ViewHolder for the command items.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The type of the view.
     * @return The created CommandViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommandViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.command_list, parent, false)
        return CommandViewHolder(itemView)
    }

    /**
     * Binds data to the ViewHolder.
     *
     * @param holder The CommandViewHolder to bind the data to.
     * @param position The position of the item in the list.
     */
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

        // SET THE ONCLICK LISTENER
        holder.itemView.setOnClickListener {
            // Handle the click event
            itemClickListener.onItemClick(commandList[position])
        }

    }

    /**
     * Returns the number of items in the list.
     *
     * @return The number of items in the list.
     */
    override fun getItemCount(): Int {
        return commandList.size
    }
}
