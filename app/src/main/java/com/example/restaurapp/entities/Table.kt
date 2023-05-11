package com.example.restaurapp.entities

import android.os.Parcel
import android.os.Parcelable


data class Table(
    val idRestaurant: String? = null,
    val number: Int? = null,
    val info: String? = null,
    var documentId: String? = null
)