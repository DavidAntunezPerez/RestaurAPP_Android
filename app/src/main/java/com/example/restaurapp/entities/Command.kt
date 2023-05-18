package com.example.restaurapp.entities

data class Command(
    var id: String? = null,
    val idRestaurant: String? = null,
    val idTable: String? = null,
    var title: String? = null,
    var description: String? = null,
    val totalPrice: Double = 0.0,
    var dishesList: List<Dish>? = null
)
