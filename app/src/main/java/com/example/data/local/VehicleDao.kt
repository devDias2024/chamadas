package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VehicleCostProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicle_cost_profiles ORDER BY createdAt DESC")
    fun getAllVehicles(): Flow<List<VehicleCostProfile>>

    @Query("SELECT * FROM vehicle_cost_profiles WHERE isSelectedForSemaforo = 1 LIMIT 1")
    fun getActiveVehicle(): Flow<VehicleCostProfile?>

    @Query("SELECT * FROM vehicle_cost_profiles WHERE isSelectedForSemaforo = 1 LIMIT 1")
    suspend fun getActiveVehicleSync(): VehicleCostProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleCostProfile): Long

    @Update
    suspend fun updateVehicle(vehicle: VehicleCostProfile)

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleCostProfile)

    @Query("UPDATE vehicle_cost_profiles SET isSelectedForSemaforo = CASE WHEN id = :selectedId THEN 1 ELSE 0 END")
    suspend fun setActiveVehicle(selectedId: Long)
}
