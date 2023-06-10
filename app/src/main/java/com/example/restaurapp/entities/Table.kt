package com.example.restaurapp.entities

/**
 * Represents a table in a restaurant.
 *
 * @param idRestaurant The ID of the restaurant.
 * @param number The number of the table.
 * @param info Additional information about the table.
 * @param documentId The ID of the table document.
 */
data class Table(
    val idRestaurant: String? = null,
    val number: Int? = null,
    val info: String? = null,
    var documentId: String? = null
)