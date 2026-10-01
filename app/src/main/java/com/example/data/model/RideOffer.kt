package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ride_offers")
data class RideOffer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appName: String, // "Uber", "99", "inDrive"
    val grossAmount: Double,
    val pickupDistanceKm: Double,
    val pickupDurationMin: Int,
    val rideDistanceKm: Double,
    val rideDurationMin: Int,
    val totalDistanceKm: Double,
    val totalDurationMin: Int,
    val passengerName: String = "Passageiro",
    val passengerRating: Double = 4.90,
    val originAddress: String,
    val destinationAddress: String,
    val isNewPassenger: Boolean = false,
    val hasStops: Boolean = false,
    val status: String, // "ACEITAR", "ATENÇÃO", "RECUSAR"
    val netProfit: Double,
    val profitMarginPercent: Double,
    val ratePerKm: Double,
    val ratePerHour: Double,
    val reasonsSummary: String = "",
    val isAccepted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
