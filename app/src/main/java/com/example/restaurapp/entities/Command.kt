package com.example.restaurapp.entities

import android.os.Parcel
import android.os.Parcelable

/**
 * Represents a command for a restaurant table.
 *
 * @param id The ID of the command.
 * @param idRestaurant The ID of the restaurant.
 * @param idTable The ID of the table.
 * @param title The title of the command.
 * @param description The description of the command.
 * @param totalPrice The total price of the command.
 * @param dishesList The list of dishes in the command.
 */
data class Command(
    var id: String? = null,
    val idRestaurant: String? = null,
    val idTable: String? = null,
    var title: String? = null,
    var description: String? = null,
    val totalPrice: Double = 0.0,
    var dishesList: List<Dish>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        // Read properties from parcel
    )

    /**
     * Write the properties of the command to a parcel.
     *
     * @param parcel The parcel to write to.
     * @param flags Additional flags.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // Write properties to parcel
    }

    /**
     * Describe the contents of the command.
     *
     * @return An integer representing the contents.
     */
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Command> {
        /**
         * Create a command from a parcel.
         *
         * @param parcel The parcel to create from.
         * @return The created command.
         */
        override fun createFromParcel(parcel: Parcel): Command {
            return Command(parcel)
        }

        /**
         * Create a new array of commands.
         *
         * @param size The size of the array.
         * @return An array of commands.
         */
        override fun newArray(size: Int): Array<Command?> {
            return arrayOfNulls(size)
        }
    }
}
