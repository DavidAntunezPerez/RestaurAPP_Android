package com.example.restaurapp.firestore

data class Dish(
    val id: String? = null,
    val idRestaurant: String? = null,
    val name: String? = null,
    val price: Double? = null,
    val image: String? = null
)
