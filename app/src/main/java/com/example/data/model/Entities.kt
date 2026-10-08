package com.example.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "states")
data class StateEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val slug: String,
    val code: String,
    val capital: String,
    @ColumnInfo(name = "best_season") val bestSeason: String,
    val description: String,
    @ColumnInfo(name = "total_spots") val totalSpots: Int,
    @ColumnInfo(name = "total_hidden") val totalHidden: Int
)

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "state_id") val stateId: Int,
    @ColumnInfo(name = "state_name") val stateName: String,
    @ColumnInfo(name = "district_id") val districtId: Int? = null,
    @ColumnInfo(name = "city_id") val cityId: Int? = null,
    val name: String,
    val slug: String,
    val description: String? = null,
    val category: String,
    @ColumnInfo(name = "image_url") val imageUrl: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @ColumnInfo(name = "best_season") val bestSeason: String? = null,
    val duration: String? = null,
    @ColumnInfo(name = "entry_fee") val entryFee: String? = null,
    @ColumnInfo(name = "vr_available") val vrAvailable: Boolean = false,
    @ColumnInfo(name = "is_hidden") val isHidden: Boolean = false,
    @ColumnInfo(name = "code_in_pdf") val codeInPdf: String,
    val source: String = "PDF_DATASET", // PDF_DATASET, API, ADMIN, OTHER
    @ColumnInfo(name = "associated_tourist_spot_id") val associatedTouristSpotId: Int? = null
)

@Entity(tableName = "vr_experiences")
data class VRExperienceEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "place_id") val placeId: Int,
    @ColumnInfo(name = "spot_name") val spotName: String,
    val title: String,
    val provider: String,
    @ColumnInfo(name = "panorama_id") val panoramaId: String,
    val latitude: Double,
    val longitude: Double,
    val available: Boolean = true,
    @ColumnInfo(name = "source_url") val sourceUrl: String
)

@Entity(tableName = "hotels")
data class HotelEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "state_id") val stateId: Int,
    val name: String,
    val type: String, // "Heritage Palace", "Luxury Hill Resort", "Boutique Homestay", "Eco Nature Camp"
    val rating: Double,
    @ColumnInfo(name = "price_per_night") val pricePerNight: Int,
    val location: String,
    val amenities: String,
    @ColumnInfo(name = "image_url") val imageUrl: String? = null
)

@Entity(tableName = "travel_options")
data class TravelOptionEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "state_id") val stateId: Int,
    val mode: String, // "Flight", "Vande Bharat Express", "Luxury Sleeper Coach", "Private SUV"
    val provider: String,
    val duration: String,
    val price: Int,
    @ColumnInfo(name = "vehicle_details") val vehicleDetails: String
)

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "booking_ref") val bookingRef: String,
    @ColumnInfo(name = "state_name") val stateName: String,
    @ColumnInfo(name = "tourist_spot_names") val touristSpotNames: String,
    @ColumnInfo(name = "hidden_spot_names") val hiddenSpotNames: String,
    @ColumnInfo(name = "hotel_name") val hotelName: String,
    @ColumnInfo(name = "travel_mode") val travelMode: String,
    @ColumnInfo(name = "total_amount") val totalAmount: Int,
    val status: String = "CONFIRMED",
    @ColumnInfo(name = "travel_date") val travelDate: String,
    @ColumnInfo(name = "travelers_count") val travelersCount: Int = 2,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist")
data class WishlistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "place_id") val placeId: Int,
    @ColumnInfo(name = "place_name") val placeName: String,
    @ColumnInfo(name = "state_name") val stateName: String,
    val category: String,
    @ColumnInfo(name = "is_hidden") val isHidden: Boolean,
    @ColumnInfo(name = "added_at") val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_itineraries")
data class CustomItineraryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    @ColumnInfo(name = "start_date") val startDate: String,
    @ColumnInfo(name = "end_date") val endDate: String,
    @ColumnInfo(name = "duration_days") val durationDays: Int,
    @ColumnInfo(name = "travelers_count") val travelersCount: Int,
    @ColumnInfo(name = "destination_names") val destinationNames: String,
    @ColumnInfo(name = "destination_ids") val destinationIds: String,
    @ColumnInfo(name = "states_visited") val statesVisited: String,
    @ColumnInfo(name = "total_cost") val totalCost: Int,
    @ColumnInfo(name = "hotel_type") val hotelType: String = "Heritage & Boutique",
    @ColumnInfo(name = "travel_mode") val travelMode: String = "Vande Bharat Express",
    val notes: String = "",
    val status: String = "SAVED",
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)

