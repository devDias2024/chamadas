package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.SemaforoSettings
import com.example.data.model.VehicleCostProfile
import com.example.domain.SemaforoEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `launch MainActivity`() {
        Robolectric.buildActivity(MainActivity::class.java).setup()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Rota Pro", appName)
    }

    @Test
    fun `evaluate profitable ride returns ACEITAR`() {
        val vehicle = VehicleCostProfile(
            name = "Onix",
            vehicleType = "Financiado",
            calculatedCostPerKm = 1.11
        )
        val settings = SemaforoSettings()

        val eval = SemaforoEngine.evaluateRide(
            appName = "Uber",
            grossAmount = 30.0,
            pickupDistanceKm = 1.0,
            pickupDurationMin = 4,
            rideDistanceKm = 9.0,
            rideDurationMin = 16,
            passengerRating = 4.95,
            passengerName = "Lucas",
            originAddress = "Av Principal",
            destinationAddress = "Rua Central",
            isNewPassenger = false,
            hasStops = false,
            vehicle = vehicle,
            settings = settings,
            riskKeywords = emptyList()
        )

        assertEquals("ACEITAR", eval.status)
        assertTrue(eval.ratePerKm >= 2.10)
        assertTrue(eval.netProfit > 15.0)
    }

    @Test
    fun `evaluate low rate ride returns RECUSAR`() {
        val vehicle = VehicleCostProfile(
            name = "Onix",
            vehicleType = "Financiado",
            calculatedCostPerKm = 1.11
        )
        val settings = SemaforoSettings()

        val eval = SemaforoEngine.evaluateRide(
            appName = "Uber",
            grossAmount = 7.0,
            pickupDistanceKm = 3.0,
            pickupDurationMin = 10,
            rideDistanceKm = 4.0,
            rideDurationMin = 15,
            passengerRating = 4.9,
            passengerName = "Passageiro",
            originAddress = "Rua A",
            destinationAddress = "Rua B",
            isNewPassenger = false,
            hasStops = false,
            vehicle = vehicle,
            settings = settings,
            riskKeywords = emptyList()
        )

        assertEquals("RECUSAR", eval.status)
    }
}
