package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dashcam_recordings")
data class DashcamRecording(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val durationFormatted: String, // e.g. "12:34"
    val sizeFormatted: String, // e.g. "410 MB"
    val cameraFacing: String, // "Traseira", "Frontal", "Ambas"
    val quality: String, // "1080p", "720p"
    val timestamp: Long = System.currentTimeMillis()
)
