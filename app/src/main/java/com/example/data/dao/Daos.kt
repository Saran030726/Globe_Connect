package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BookingEntity
import com.example.data.model.CustomItineraryEntity
import com.example.data.model.HotelEntity
import com.example.data.model.PlaceEntity
import com.example.data.model.StateEntity
import com.example.data.model.TravelOptionEntity
import com.example.data.model.VRExperienceEntity
import com.example.data.model.WishlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StateDao {
    @Query("SELECT * FROM states ORDER BY id ASC")
    fun getAllStates(): Flow<List<StateEntity>>

    @Query("SELECT * FROM states WHERE id = :id LIMIT 1")
    fun getStateById(id: Int): Flow<StateEntity?>

    @Query("SELECT * FROM states WHERE name = :name LIMIT 1")
    suspend fun getStateByName(name: String): StateEntity?

    @Query("SELECT COUNT(*) FROM states")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(states: List<StateEntity>)
}

@Dao
interface PlaceDao {
    @Query("SELECT * FROM places WHERE state_id = :stateId ORDER BY is_hidden ASC, id ASC")
    fun getPlacesByState(stateId: Int): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE state_id = :stateId AND is_hidden = 0 ORDER BY id ASC")
    fun getTouristSpotsByState(stateId: Int): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE state_id = :stateId AND is_hidden = 1 ORDER BY id ASC")
    fun getHiddenSpotsByState(stateId: Int): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE id = :id LIMIT 1")
    fun getPlaceById(id: Int): Flow<PlaceEntity?>

    @Query("SELECT * FROM places WHERE id = :id LIMIT 1")
    suspend fun getPlaceByIdDirect(id: Int): PlaceEntity?

    @Query("SELECT * FROM places WHERE associated_tourist_spot_id = :touristSpotId AND is_hidden = 1")
    fun getAssociatedHiddenSpots(touristSpotId: Int): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE associated_tourist_spot_id = :touristSpotId AND is_hidden = 1")
    suspend fun getAssociatedHiddenSpotsDirect(touristSpotId: Int): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE vr_available = 1")
    fun getVRAvailablePlaces(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE name LIKE '%' || :query || '%' OR state_name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY is_hidden ASC, name ASC LIMIT :limit")
    fun searchPlaces(query: String, limit: Int = 100): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places ORDER BY id ASC LIMIT :limit OFFSET :offset")
    fun getPlacesPaged(limit: Int, offset: Int): Flow<List<PlaceEntity>>

    @Query("SELECT COUNT(*) FROM places")
    suspend fun getCount(): Int

    @Query("SELECT MAX(id) FROM places")
    suspend fun getMaxId(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<PlaceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity): Long

    @Update
    suspend fun updatePlace(place: PlaceEntity)

    @Delete
    suspend fun deletePlace(place: PlaceEntity)
}

@Dao
interface VRExperienceDao {
    @Query("SELECT * FROM vr_experiences ORDER BY id ASC")
    fun getAllExperiences(): Flow<List<VRExperienceEntity>>

    @Query("SELECT * FROM vr_experiences WHERE place_id = :placeId LIMIT 1")
    fun getExperienceByPlaceId(placeId: Int): Flow<VRExperienceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(experiences: List<VRExperienceEntity>)
}

@Dao
interface HotelDao {
    @Query("SELECT * FROM hotels WHERE state_id = :stateId ORDER BY rating DESC")
    fun getHotelsByState(stateId: Int): Flow<List<HotelEntity>>

    @Query("SELECT * FROM hotels ORDER BY rating DESC")
    fun getAllHotels(): Flow<List<HotelEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(hotels: List<HotelEntity>)
}

@Dao
interface TravelOptionDao {
    @Query("SELECT * FROM travel_options WHERE state_id = :stateId ORDER BY price ASC")
    fun getTravelOptionsByState(stateId: Int): Flow<List<TravelOptionEntity>>

    @Query("SELECT * FROM travel_options ORDER BY price ASC")
    fun getAllTravelOptions(): Flow<List<TravelOptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(options: List<TravelOptionEntity>)
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY id DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    fun getBookingById(id: Long): Flow<BookingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist ORDER BY id DESC")
    fun getAllWishlist(): Flow<List<WishlistEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist WHERE place_id = :placeId LIMIT 1)")
    fun isWishlisted(placeId: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlist(item: WishlistEntity)

    @Query("DELETE FROM wishlist WHERE place_id = :placeId")
    suspend fun deleteByPlaceId(placeId: Int)
}

@Dao
interface ItineraryDao {
    @Query("SELECT * FROM custom_itineraries ORDER BY created_at DESC")
    fun getAllItineraries(): Flow<List<CustomItineraryEntity>>

    @Query("SELECT * FROM custom_itineraries WHERE id = :id LIMIT 1")
    fun getItineraryById(id: Long): Flow<CustomItineraryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItinerary(itinerary: CustomItineraryEntity): Long

    @Update
    suspend fun updateItinerary(itinerary: CustomItineraryEntity)

    @Delete
    suspend fun deleteItinerary(itinerary: CustomItineraryEntity)

    @Query("DELETE FROM custom_itineraries WHERE id = :id")
    suspend fun deleteItineraryById(id: Long)

    @Query("SELECT COUNT(*) FROM custom_itineraries")
    suspend fun getCount(): Int
}

