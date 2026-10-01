package com.example.domain

import com.example.data.model.RideOffer
import com.example.data.model.RiskKeyword
import com.example.data.model.SemaforoSettings
import com.example.data.model.VehicleCostProfile
import java.util.Locale

data class EvaluationResult(
    val status: String, // "ACEITAR", "ATENÇÃO", "RECUSAR"
    val netProfit: Double,
    val profitMarginPercent: Double,
    val ratePerKm: Double,
    val ratePerHour: Double,
    val totalDistanceKm: Double,
    val totalDurationMin: Int,
    val reasons: List<String>,
    val hasRiskAlert: Boolean,
    val hasSupermarketAlert: Boolean,
    val isPassengerRisk: Boolean,
    val isPickupTooFar: Boolean
)

object SemaforoEngine {

    fun evaluateRide(
        appName: String,
        grossAmount: Double,
        pickupDistanceKm: Double,
        pickupDurationMin: Int,
        rideDistanceKm: Double,
        rideDurationMin: Int,
        passengerRating: Double,
        passengerName: String,
        originAddress: String,
        destinationAddress: String,
        isNewPassenger: Boolean,
        hasStops: Boolean,
        vehicle: VehicleCostProfile?,
        settings: SemaforoSettings,
        riskKeywords: List<RiskKeyword>
    ): EvaluationResult {
        val totalDistanceKm = (pickupDistanceKm + rideDistanceKm).coerceAtLeast(0.1)
        val totalDurationMin = (pickupDurationMin + rideDurationMin).coerceAtLeast(1)

        val costPerKm = vehicle?.calculatedCostPerKm ?: 1.11
        val realTotalCost = totalDistanceKm * costPerKm
        val netProfit = grossAmount - realTotalCost
        val profitMarginPercent = if (grossAmount > 0) (netProfit / grossAmount) * 100.0 else 0.0

        val ratePerKm = grossAmount / totalDistanceKm
        val ratePerHour = grossAmount / (totalDurationMin / 60.0)

        val reasons = mutableListOf<String>()
        var isRed = false
        var isYellow = false

        // 1. Check Risk Addresses
        val originLower = originAddress.lowercase(Locale.ROOT)
        val destLower = destinationAddress.lowercase(Locale.ROOT)

        var hasRiskAlert = false
        if (settings.riskAddressAlertEnabled) {
            val pickupKeywords = riskKeywords.filter { it.targetType == "EMBARQUE" }
            val destKeywords = riskKeywords.filter { it.targetType == "DESTINO" }

            val matchedPickup = pickupKeywords.firstOrNull { originLower.contains(it.word.lowercase(Locale.ROOT)) }
            val matchedDest = destKeywords.firstOrNull { destLower.contains(it.word.lowercase(Locale.ROOT)) }

            if (matchedPickup != null) {
                hasRiskAlert = true
                isRed = true
                reasons.add("Embarque em área de risco: ${matchedPickup.word}")
            }
            if (matchedDest != null) {
                hasRiskAlert = true
                isRed = true
                reasons.add("Destino em área de risco: ${matchedDest.word}")
            }
        }

        // 2. Check Supermarkets
        var hasSupermarketAlert = false
        if (settings.supermarketAlertEnabled) {
            val marketKeywords = riskKeywords.filter { it.targetType == "MERCADOS" }
            val marketNames = listOf("mercado", "supermercado", "atacadão", "atacadao", "assai", "assaí", "carrefour", "hipermercado")

            val matchedSpecific = marketKeywords.firstOrNull { originLower.contains(it.word.lowercase(Locale.ROOT)) }
            val matchedGeneric = marketNames.firstOrNull { originLower.contains(it) }

            if (matchedSpecific != null || matchedGeneric != null) {
                hasSupermarketAlert = true
                isRed = true
                val term = matchedSpecific?.word ?: matchedGeneric ?: "Supermercado"
                reasons.add("Alerta de Supermercado: $term (risco de espera e cancelamento)")
            }
        }

        // 3. Passenger Rating Check
        var isPassengerRisk = false
        if (passengerRating < settings.minPassengerRating) {
            isPassengerRisk = true
            isRed = true
            reasons.add("Nota do passageiro (★ ${String.format(Locale.ROOT, "%.2f", passengerRating)}) abaixo do mínimo (${String.format(Locale.ROOT, "%.2f", settings.minPassengerRating)})")
        }

        // 4. Stops Check
        if (hasStops && settings.stopsAlertEnabled) {
            isYellow = true
            reasons.add("Corrida com múltiplas paradas cadastradas")
        }

        // 5. New Passenger Badge
        if (isNewPassenger && settings.newPassengerBadgeEnabled) {
            reasons.add("Usuário iniciante no app")
        }

        // 6. Minimum Trip Gross Value
        if (grossAmount < settings.minRideValue) {
            isRed = true
            reasons.add("Valor total (R$ ${String.format(Locale.ROOT, "%.2f", grossAmount)}) abaixo do mínimo (R$ ${String.format(Locale.ROOT, "%.2f", settings.minRideValue)})")
        }

        // 7. Minimum Profit Check
        if (netProfit < settings.minProfitValue) {
            isRed = true
            reasons.add("Lucro líquido (R$ ${String.format(Locale.ROOT, "%.2f", netProfit)}) abaixo do mínimo estipulado (R$ ${String.format(Locale.ROOT, "%.2f", settings.minProfitValue)})")
        }

        // 8. Profit Margin Check
        if (profitMarginPercent < settings.minProfitPercent) {
            isRed = true
            reasons.add("Margem de lucro (${String.format(Locale.ROOT, "%.1f", profitMarginPercent)}%) abaixo da meta de ${String.format(Locale.ROOT, "%.0f", settings.minProfitPercent)}%")
        }

        // 9. Pickup Distance & Time Limits
        var isPickupTooFar = false
        if (pickupDistanceKm > settings.maxPickupDistanceKm) {
            isPickupTooFar = true
            isYellow = true
            reasons.add("Embarque longe (${String.format(Locale.ROOT, "%.1f", pickupDistanceKm)} km)")
        }
        if (pickupDurationMin > settings.maxPickupTimeMinutes) {
            isYellow = true
            reasons.add("Tempo até embarque elevado (${pickupDurationMin} min)")
        }

        // 10. Rate per Km & Rate per Hour Thresholds
        if (!isRed) {
            val isGreenKm = ratePerKm >= settings.minRatePerKmGreen
            val isGreenHour = ratePerHour >= settings.minRatePerHourGreen
            val isYellowKm = ratePerKm >= settings.minRatePerKmYellow
            val isYellowHour = ratePerHour >= settings.minRatePerHourYellow

            if (isGreenKm && isGreenHour && !isYellow) {
                reasons.add(0, "Corrida lucrativa! Atende todas as suas metas de ganho")
            } else if (isYellowKm && isYellowHour) {
                isYellow = true
                reasons.add(0, "Na média. Avalie esforço e trânsito da região")
            } else {
                isRed = true
                reasons.add(0, "R$/km ou R$/hora abaixo da sua faixa mínima")
            }
        }

        val status = when {
            isRed -> "RECUSAR"
            isYellow -> "ATENÇÃO"
            else -> "ACEITAR"
        }

        return EvaluationResult(
            status = status,
            netProfit = netProfit,
            profitMarginPercent = profitMarginPercent,
            ratePerKm = ratePerKm,
            ratePerHour = ratePerHour,
            totalDistanceKm = totalDistanceKm,
            totalDurationMin = totalDurationMin,
            reasons = reasons,
            hasRiskAlert = hasRiskAlert,
            hasSupermarketAlert = hasSupermarketAlert,
            isPassengerRisk = isPassengerRisk,
            isPickupTooFar = isPickupTooFar
        )
    }

    fun toRideOffer(
        appName: String,
        grossAmount: Double,
        pickupDistanceKm: Double,
        pickupDurationMin: Int,
        rideDistanceKm: Double,
        rideDurationMin: Int,
        passengerRating: Double,
        passengerName: String,
        originAddress: String,
        destinationAddress: String,
        isNewPassenger: Boolean,
        hasStops: Boolean,
        vehicle: VehicleCostProfile?,
        settings: SemaforoSettings,
        riskKeywords: List<RiskKeyword>,
        isAccepted: Boolean = false
    ): RideOffer {
        val eval = evaluateRide(
            appName, grossAmount, pickupDistanceKm, pickupDurationMin,
            rideDistanceKm, rideDurationMin, passengerRating, passengerName,
            originAddress, destinationAddress, isNewPassenger, hasStops,
            vehicle, settings, riskKeywords
        )
        return RideOffer(
            appName = appName,
            grossAmount = grossAmount,
            pickupDistanceKm = pickupDistanceKm,
            pickupDurationMin = pickupDurationMin,
            rideDistanceKm = rideDistanceKm,
            rideDurationMin = rideDurationMin,
            totalDistanceKm = eval.totalDistanceKm,
            totalDurationMin = eval.totalDurationMin,
            passengerName = passengerName,
            passengerRating = passengerRating,
            originAddress = originAddress,
            destinationAddress = destinationAddress,
            isNewPassenger = isNewPassenger,
            hasStops = hasStops,
            status = eval.status,
            netProfit = eval.netProfit,
            profitMarginPercent = eval.profitMarginPercent,
            ratePerKm = eval.ratePerKm,
            ratePerHour = eval.ratePerHour,
            reasonsSummary = eval.reasons.joinToString(" • "),
            isAccepted = isAccepted,
            timestamp = System.currentTimeMillis()
        )
    }
}
