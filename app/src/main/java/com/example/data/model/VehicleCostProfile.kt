package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle_cost_profiles")
data class VehicleCostProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val vehicleType: String, // "Financiado", "Alugado", "Quitado"
    val isSelectedForSemaforo: Boolean = false,
    val fuelPricePerLiter: Double = 5.89,
    val fuelConsumptionKmPerL: Double = 12.5,
    val monthlyFinancingInstallment: Double = 1490.0,
    val rentalRateMonthly: Double = 0.0,
    val monthlyMaintenanceCost: Double = 350.0,
    val monthlyDepreciation: Double = 780.0,
    val monthlyInsuranceAndTax: Double = 360.0,
    val monthlyKmDriven: Double = 4500.0,
    val calculatedCostPerKm: Double = 1.11,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun calculateMonthlyCost(): Double {
        val fuelMonthly = if (fuelConsumptionKmPerL > 0) {
            (monthlyKmDriven / fuelConsumptionKmPerL) * fuelPricePerLiter
        } else 0.0

        val acquisitionCost = when (vehicleType) {
            "Financiado" -> monthlyFinancingInstallment
            "Alugado" -> rentalRateMonthly
            else -> 0.0
        }

        return fuelMonthly + acquisitionCost + monthlyMaintenanceCost + monthlyDepreciation + monthlyInsuranceAndTax
    }

    fun calculateCostPerKm(): Double {
        val totalMonthly = calculateMonthlyCost()
        return if (monthlyKmDriven > 0) totalMonthly / monthlyKmDriven else 1.0
    }
}
