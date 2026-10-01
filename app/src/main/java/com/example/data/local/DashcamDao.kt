package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DashcamRecording
import kotlinx.coroutines.flow.Flow

@Dao
interface DashcamDao {
    @Query("SELECT * FROM dashcam_recordings ORDER BY timestamp DESC")
    fun getAllRecordings(): Flow<List<DashcamRecording>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecording(recording: DashcamRecording): Long

    @Delete
    suspend fun deleteRecording(recording: DashcamRecording)

    @Query("DELETE FROM dashcam_recordings")
    suspend fun clearAll()
}
