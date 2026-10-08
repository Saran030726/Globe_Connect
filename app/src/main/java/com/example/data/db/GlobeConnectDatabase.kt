package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.BookingDao
import com.example.data.dao.HotelDao
import com.example.data.dao.ItineraryDao
import com.example.data.dao.PlaceDao
import com.example.data.dao.StateDao
import com.example.data.dao.TravelOptionDao
import com.example.data.dao.VRExperienceDao
import com.example.data.dao.WishlistDao
import com.example.data.model.BookingEntity
import com.example.data.model.CustomItineraryEntity
import com.example.data.model.HotelEntity
import com.example.data.model.PlaceEntity
import com.example.data.model.StateEntity
import com.example.data.model.TravelOptionEntity
import com.example.data.model.VRExperienceEntity
import com.example.data.model.WishlistEntity

@Database(
    entities = [
        StateEntity::class,
        PlaceEntity::class,
        VRExperienceEntity::class,
        HotelEntity::class,
        TravelOptionEntity::class,
        BookingEntity::class,
        WishlistEntity::class,
        CustomItineraryEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class GlobeConnectDatabase : RoomDatabase() {
    abstract fun stateDao(): StateDao
    abstract fun placeDao(): PlaceDao
    abstract fun vrExperienceDao(): VRExperienceDao
    abstract fun hotelDao(): HotelDao
    abstract fun travelOptionDao(): TravelOptionDao
    abstract fun bookingDao(): BookingDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun itineraryDao(): ItineraryDao

    companion object {
        @Volatile
        private var INSTANCE: GlobeConnectDatabase? = null

        fun getInstance(context: Context): GlobeConnectDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GlobeConnectDatabase::class.java,
                    "globe_connect_app.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
