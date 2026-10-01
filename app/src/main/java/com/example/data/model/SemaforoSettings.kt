package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "semaforo_settings")
data class SemaforoSettings(
    @PrimaryKey
    val id: Int = 1,
    val minRatePerKmGreen: Double = 2.10,
    val minRatePerKmYellow: Double = 1.60,
    val minRatePerHourGreen: Double = 50.0,
    val minRatePerHourYellow: Double = 35.0,
    val minPassengerRating: Double = 4.80,
    val minRideValue: Double = 10.0,
    val minProfitValue: Double = 5.0,
    val minProfitPercent: Double = 40.0,
    val maxPickupTimeMinutes: Int = 8,
    val maxPickupDistanceKm: Double = 3.5,
    val maxTotalDistanceKm: Double = 35.0,
    val cardLayoutType: String = "COMPLETO", // "COMPLETO" or "SECUNDARIO"
    val voiceNotificationEnabled: Boolean = true,
    val autoCopyChatEnabled: Boolean = true,
    val chatTemplate: String = "Boa tarde {nome}! Já estou a caminho e chegarei em aproximadamente {tempo} min, por gentileza me aguarde no local de embarque.",
    val riskAddressAlertEnabled: Boolean = true,
    val supermarketAlertEnabled: Boolean = true,
    val newPassengerBadgeEnabled: Boolean = true,
    val stopsAlertEnabled: Boolean = true,
    val dashcamDiscreetEnabled: Boolean = false
)
