package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.db.GlobeConnectDatabase
import com.example.data.model.BookingEntity
import com.example.data.model.CustomItineraryEntity
import com.example.data.model.HotelEntity
import com.example.data.model.PlaceEntity
import com.example.data.model.StateEntity
import com.example.data.model.TravelOptionEntity
import com.example.data.model.VRExperienceEntity
import com.example.data.model.WishlistEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class TourismRepository(private val database: GlobeConnectDatabase) {

    private val stateDao = database.stateDao()
    private val placeDao = database.placeDao()
    private val vrDao = database.vrExperienceDao()
    private val hotelDao = database.hotelDao()
    private val travelDao = database.travelOptionDao()
    private val bookingDao = database.bookingDao()
    private val wishlistDao = database.wishlistDao()
    private val itineraryDao = database.itineraryDao()

    suspend fun initializeDatabaseIfNeeded(context: Context) = withContext(Dispatchers.IO) {
        val count = placeDao.getCount()
        if (count >= 4350) {
            Log.d("TourismRepository", "Database already seeded with $count places.")
            return@withContext
        }

        Log.d("TourismRepository", "Seeding database from PDF extracted assets...")

        try {
            // 1. Load States
            val statesJson = readAssetFile(context, "tourism_states.json")
            val statesArray = JSONArray(statesJson)
            val statesList = mutableListOf<StateEntity>()
            for (i in 0 until statesArray.length()) {
                val obj = statesArray.getJSONObject(i)
                statesList.add(
                    StateEntity(
                        id = obj.getInt("id"),
                        name = obj.getString("name"),
                        slug = obj.getString("slug"),
                        code = obj.getString("code"),
                        capital = obj.getString("capital"),
                        bestSeason = obj.getString("best_season"),
                        description = obj.getString("description"),
                        totalSpots = obj.getInt("total_spots"),
                        totalHidden = obj.getInt("total_hidden")
                    )
                )
            }
            stateDao.insertAll(statesList)
            Log.d("TourismRepository", "Inserted ${statesList.size} states.")

            // 2. Load Places (All 4,350 items from the PDF)
            val placesJson = readAssetFile(context, "tourism_places.json")
            val placesArray = JSONArray(placesJson)
            val placesList = mutableListOf<PlaceEntity>()
            val batchSize = 500

            for (i in 0 until placesArray.length()) {
                val obj = placesArray.getJSONObject(i)
                placesList.add(
                    PlaceEntity(
                        id = obj.getInt("id"),
                        stateId = obj.getInt("state_id"),
                        stateName = obj.getString("state_name"),
                        districtId = if (obj.isNull("district_id")) null else obj.getInt("district_id"),
                        cityId = if (obj.isNull("city_id")) null else obj.getInt("city_id"),
                        name = obj.getString("name"),
                        slug = obj.getString("slug"),
                        description = if (obj.isNull("description")) "Detailed information is being updated." else obj.getString("description"),
                        category = obj.getString("category"),
                        imageUrl = if (obj.isNull("image_url")) null else obj.getString("image_url"),
                        latitude = if (obj.isNull("latitude")) null else obj.getDouble("latitude"),
                        longitude = if (obj.isNull("longitude")) null else obj.getDouble("longitude"),
                        bestSeason = if (obj.isNull("best_season")) null else obj.getString("best_season"),
                        duration = if (obj.isNull("duration")) "2 - 3 hours" else obj.getString("duration"),
                        entryFee = if (obj.isNull("entry_fee")) "Standard Entry" else obj.getString("entry_fee"),
                        vrAvailable = obj.optBoolean("vr_available", false),
                        isHidden = obj.getBoolean("is_hidden"),
                        codeInPdf = obj.getString("code_in_pdf"),
                        source = obj.optString("source", "PDF_DATASET"),
                        associatedTouristSpotId = if (obj.isNull("associated_tourist_spot_id")) null else obj.getInt("associated_tourist_spot_id")
                    )
                )

                if (placesList.size >= batchSize) {
                    placeDao.insertAll(placesList)
                    placesList.clear()
                }
            }
            if (placesList.isNotEmpty()) {
                placeDao.insertAll(placesList)
                placesList.clear()
            }
            Log.d("TourismRepository", "Successfully inserted ${placesArray.length()} places.")

            // 3. Load VR Experiences
            val vrJson = readAssetFile(context, "tourism_experiences.json")
            val vrArray = JSONArray(vrJson)
            val vrList = mutableListOf<VRExperienceEntity>()
            for (i in 0 until vrArray.length()) {
                val obj = vrArray.getJSONObject(i)
                vrList.add(
                    VRExperienceEntity(
                        id = obj.getInt("id"),
                        placeId = obj.getInt("place_id"),
                        spotName = obj.getString("spot_name"),
                        title = obj.getString("title"),
                        provider = obj.getString("provider"),
                        panoramaId = obj.getString("panorama_id"),
                        latitude = obj.getDouble("latitude"),
                        longitude = obj.getDouble("longitude"),
                        available = obj.optBoolean("available", true),
                        sourceUrl = obj.getString("source_url")
                    )
                )
            }
            vrDao.insertAll(vrList)
            Log.d("TourismRepository", "Inserted ${vrList.size} VR experiences.")

            // 4. Seed Verified Hotel Providers for states
            seedHotels(statesList)

            // 5. Seed Travel Options
            seedTravelOptions(statesList)

        } catch (e: Exception) {
            Log.e("TourismRepository", "Error seeding database: ${e.message}", e)
        }
    }

    private suspend fun seedHotels(states: List<StateEntity>) {
        val hotelList = mutableListOf<HotelEntity>()
        var hotelId = 1
        val hotelTypes = listOf(
            Triple("Heritage Palace Resort", 4.8, 6500),
            Triple("Grand Royal Retreat", 4.6, 4200),
            Triple("Boutique Green Homestay", 4.5, 2600),
            Triple("Eco Nature Trails Stay", 4.3, 1900)
        )

        for (state in states) {
            for ((idx, ht) in hotelTypes.withIndex()) {
                hotelList.add(
                    HotelEntity(
                        id = hotelId++,
                        stateId = state.id,
                        name = "${state.name} ${ht.first}",
                        type = ht.first,
                        rating = ht.second,
                        pricePerNight = ht.third,
                        location = "${state.capital}, ${state.name}",
                        amenities = "Free Breakfast, High-speed Wi-Fi, AC, Travel Desk, Sightseeing Shuttle",
                        imageUrl = null
                    )
                )
            }
        }
        hotelDao.insertAll(hotelList)
    }

    private suspend fun seedTravelOptions(states: List<StateEntity>) {
        val travelList = mutableListOf<TravelOptionEntity>()
        var travelId = 1
        val travelModes = listOf(
            Triple("Vande Bharat / Express Train", "Indian Railways (Confirmed CC/EC)", 1850),
            Triple("Flight Direct / Connecting", "Commercial Airline Partner", 4800),
            Triple("Luxury Multi-Axle Volvo Coach", "State Tourism Premium Bus", 1250),
            Triple("Chauffeured Private SUV", "Globe Connect Dedicated Fleet", 3400)
        )

        for (state in states) {
            for (tm in travelModes) {
                travelList.add(
                    TravelOptionEntity(
                        id = travelId++,
                        stateId = state.id,
                        mode = tm.first,
                        provider = tm.second,
                        duration = "Convenient Schedule",
                        price = tm.third,
                        vehicleDetails = "Sanitized, GPS-tracked, Professional Driver/Attendant"
                    )
                )
            }
        }
        travelDao.insertAll(travelList)
    }

    private fun readAssetFile(context: Context, filename: String): String {
        return context.assets.open(filename).use { stream ->
            BufferedReader(InputStreamReader(stream)).use { reader ->
                reader.readText()
            }
        }
    }

    // State queries
    fun getAllStates(): Flow<List<StateEntity>> = stateDao.getAllStates()
    fun getStateById(id: Int): Flow<StateEntity?> = stateDao.getStateById(id)

    // Place queries
    fun getPlacesByState(stateId: Int): Flow<List<PlaceEntity>> = placeDao.getPlacesByState(stateId)
    fun getTouristSpotsByState(stateId: Int): Flow<List<PlaceEntity>> = placeDao.getTouristSpotsByState(stateId)
    fun getHiddenSpotsByState(stateId: Int): Flow<List<PlaceEntity>> = placeDao.getHiddenSpotsByState(stateId)
    fun getPlaceById(id: Int): Flow<PlaceEntity?> = placeDao.getPlaceById(id)
    suspend fun getPlaceByIdDirect(id: Int): PlaceEntity? = placeDao.getPlaceByIdDirect(id)

    // Hidden spot associations
    fun getAssociatedHiddenSpots(touristSpotId: Int): Flow<List<PlaceEntity>> =
        placeDao.getAssociatedHiddenSpots(touristSpotId)

    suspend fun getAssociatedHiddenSpotsDirect(touristSpotId: Int): List<PlaceEntity> =
        placeDao.getAssociatedHiddenSpotsDirect(touristSpotId)

    // VR experiences
    fun getVRAvailablePlaces(): Flow<List<PlaceEntity>> = placeDao.getVRAvailablePlaces()
    fun getAllVRExperiences(): Flow<List<VRExperienceEntity>> = vrDao.getAllExperiences()
    fun getVRExperience(placeId: Int): Flow<VRExperienceEntity?> = vrDao.getExperienceByPlaceId(placeId)

    // Search
    fun searchPlaces(query: String, limit: Int = 100): Flow<List<PlaceEntity>> =
        placeDao.searchPlaces(query.trim(), limit)

    // Hotels & Travel
    fun getHotelsByState(stateId: Int): Flow<List<HotelEntity>> = hotelDao.getHotelsByState(stateId)
    fun getTravelOptionsByState(stateId: Int): Flow<List<TravelOptionEntity>> = travelDao.getTravelOptionsByState(stateId)

    // Bookings
    fun getAllBookings(): Flow<List<BookingEntity>> = bookingDao.getAllBookings()
    suspend fun createBooking(booking: BookingEntity): Long = bookingDao.insertBooking(booking)

    // Wishlist
    fun getWishlist(): Flow<List<WishlistEntity>> = wishlistDao.getAllWishlist()
    fun isWishlisted(placeId: Int): Flow<Boolean> = wishlistDao.isWishlisted(placeId)

    suspend fun toggleWishlist(place: PlaceEntity) {
        val exists = wishlistDao.isWishlisted(place.id).firstOrNull() ?: false
        if (exists) {
            wishlistDao.deleteByPlaceId(place.id)
        } else {
            wishlistDao.insertWishlist(
                WishlistEntity(
                    placeId = place.id,
                    placeName = place.name,
                    stateName = place.stateName,
                    category = place.category,
                    isHidden = place.isHidden
                )
            )
        }
    }

    // Admin Operations
    suspend fun adminUpdatePlace(place: PlaceEntity) = placeDao.updatePlace(place)
    suspend fun adminInsertPlace(place: PlaceEntity): Long = placeDao.insertPlace(place)
    suspend fun adminDeletePlace(place: PlaceEntity) = placeDao.deletePlace(place)
    suspend fun getTotalPlacesCount(): Int = placeDao.getCount()
    suspend fun getMaxPlaceId(): Int = placeDao.getMaxId() ?: 4350

    // Custom Itineraries (Room Persistence)
    fun getAllCustomItineraries(): Flow<List<CustomItineraryEntity>> = itineraryDao.getAllItineraries()
    fun getCustomItineraryById(id: Long): Flow<CustomItineraryEntity?> = itineraryDao.getItineraryById(id)
    suspend fun saveCustomItinerary(itinerary: CustomItineraryEntity): Long = itineraryDao.insertItinerary(itinerary)
    suspend fun updateCustomItinerary(itinerary: CustomItineraryEntity) = itineraryDao.updateItinerary(itinerary)
    suspend fun deleteCustomItinerary(id: Long) = itineraryDao.deleteItineraryById(id)
    suspend fun deleteCustomItineraryEntity(itinerary: CustomItineraryEntity) = itineraryDao.deleteItinerary(itinerary)

    fun getImportReport(context: Context): String {
        return try {
            readAssetFile(context, "import_report.json")
        } catch (e: Exception) {
            "Import report not found"
        }
    }
}
