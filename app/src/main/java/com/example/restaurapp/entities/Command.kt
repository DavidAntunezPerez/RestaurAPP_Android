package com.example.restaurapp.entities

import android.os.Parcel
import android.os.Parcelable

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // Write properties to parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Command> {
        override fun createFromParcel(parcel: Parcel): Command {
            return Command(parcel)
        }

        override fun newArray(size: Int): Array<Command?> {
            return arrayOfNulls(size)
        }
    }
}
