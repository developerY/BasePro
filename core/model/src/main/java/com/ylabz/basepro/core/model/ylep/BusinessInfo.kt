package com.ylabz.basepro.core.model.ylep

// Date Transfer Object
data class BusinessInfo(
    // Yelp ID of this business.
    val id: String,
    // Name of this business.
    val name: String?,
    // Web site address
    val url: String?,
    // Rating for this business (value ranges from 1, 1.5, ... 4.5, 5).
    val rating: Double?,
    // URLs of up to three photos of the business.
    val photos: List<String?>?,
    // Price level of the business. Value is one of $, $$, $$$ and $$$$ or null if we don't have price available for the business.
    val price: String?,
    // The coordinates of this business.
    val coordinates: Coordinates?,
    // A list of category title and alias pairs associated with this business.
    val categories: List<Category?>?
)