package com.example.restaurapp.entities

data class Command(
    var id: String? = null,
    val idRestaurant: String? = null,
    var title: String? = "Untitled Command",
    var description: String? = "No description provided",
    val totalPrice: Double = 0.0,
    var dishesList: List<Dish>? = null
)
