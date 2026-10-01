package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SemaforoSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM semaforo_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<SemaforoSettings?>

    @Query("SELECT * FROM semaforo_settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): SemaforoSettings?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: SemaforoSettings)

    @Update
    suspend fun updateSettings(settings: SemaforoSettings)
}
