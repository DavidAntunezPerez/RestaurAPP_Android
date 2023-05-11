package com.example.restaurapp.entities

data class Dish(
    var id: String? = null,
    val idRestaurant: String? = null,
    val name: String? = null,
    val price: Double? = null,
    val image: String? = null
)
