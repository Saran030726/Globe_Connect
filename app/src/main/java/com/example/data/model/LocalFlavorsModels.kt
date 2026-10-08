package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data model representing a popular regional dish discovered for an Indian destination.
 */
@JsonClass(generateAdapter = true)
data class LocalDish(
    @Json(name = "name") val name: String,
    @Json(name = "hindiOrRegionalName") val hindiOrRegionalName: String = "",
    @Json(name = "category") val category: String = "Main Course", // Main Course, Street Food, Dessert, Beverage, Breakfast
    @Json(name = "isVegetarian") val isVegetarian: Boolean = true,
    @Json(name = "description") val description: String,
    @Json(name = "signatureIngredients") val signatureIngredients: List<String> = emptyList(),
    @Json(name = "mustTryAt") val mustTryAt: String = "Popular local street vendors & traditional eateries",
    @Json(name = "spiceLevel") val spiceLevel: String = "Medium", // Mild, Medium, Spicy, Very Spicy
    @Json(name = "priceRange") val priceRange: String = "₹100 - ₹300"
)

/**
 * Complete culinary profile for a destination.
 */
@JsonClass(generateAdapter = true)
data class DestinationFlavors(
    @Json(name = "placeName") val placeName: String,
    @Json(name = "stateName") val stateName: String,
    @Json(name = "regionCuisineTitle") val regionCuisineTitle: String,
    @Json(name = "culinaryOverview") val culinaryOverview: String,
    @Json(name = "signatureDishes") val signatureDishes: List<LocalDish>,
    @Json(name = "streetFoodHotspots") val streetFoodHotspots: List<String> = emptyList(),
    @Json(name = "diningEtiquetteTip") val diningEtiquetteTip: String = "",
    val isAiGenerated: Boolean = true,
    val fetchedAtMillis: Long = System.currentTimeMillis()
)

sealed interface LocalFlavorsUiState {
    object Idle : LocalFlavorsUiState
    object Loading : LocalFlavorsUiState
    data class Success(val flavors: DestinationFlavors) : LocalFlavorsUiState
    data class Error(val message: String, val fallbackFlavors: DestinationFlavors? = null) : LocalFlavorsUiState
}
