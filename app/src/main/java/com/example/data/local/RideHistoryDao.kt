package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.RideOffer
import kotlinx.coroutines.flow.Flow

@Dao
interface RideHistoryDao {
    @Query("SELECT * FROM ride_offers ORDER BY timestamp DESC")
    fun getAllRides(): Flow<List<RideOffer>>

    @Query("SELECT * FROM ride_offers WHERE appName = :app ORDER BY timestamp DESC")
    fun getRidesByApp(app: String): Flow<List<RideOffer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRide(ride: RideOffer): Long

    @Delete
    suspend fun deleteRide(ride: RideOffer)

    @Query("DELETE FROM ride_offers")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM ride_offers")
    suspend fun getCount(): Int
}
