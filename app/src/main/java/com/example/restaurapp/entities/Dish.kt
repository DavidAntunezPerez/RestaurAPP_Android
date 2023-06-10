package com.example.restaurapp.entities

/**
 * Represents a dish in a restaurant.
 *
 * @param id The ID of the dish.
 * @param idRestaurant The ID of the restaurant.
 * @param name The name of the dish.
 * @param price The price of the dish.
 * @param image The image URL of the dish.
 */
data class Dish(
    var id: String? = null,
    val idRestaurant: String? = null,
    val name: String? = null,
    val price: Double? = null,
    val image: String? = null
)
