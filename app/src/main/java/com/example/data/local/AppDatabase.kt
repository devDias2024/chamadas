package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.DashcamRecording
import com.example.data.model.RideOffer
import com.example.data.model.RiskKeyword
import com.example.data.model.SemaforoSettings
import com.example.data.model.VehicleCostProfile

@Database(
    entities = [
        VehicleCostProfile::class,
        SemaforoSettings::class,
        RideOffer::class,
        RiskKeyword::class,
        DashcamRecording::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao
    abstract fun settingsDao(): SettingsDao
    abstract fun rideHistoryDao(): RideHistoryDao
    abstract fun riskKeywordDao(): RiskKeywordDao
    abstract fun dashcamDao(): DashcamDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rotapro_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(db: AppDatabase) {
            try {
                if (db.vehicleDao().getActiveVehicleSync() != null) {
                    return
                }

                // Seed Vehicles (as seen in screenshots)
                val onix = VehicleCostProfile(
                    name = "Chevrolet Onix 1.0",
                    vehicleType = "Financiado",
                    isSelectedForSemaforo = true,
                    fuelPricePerLiter = 5.89,
                    fuelConsumptionKmPerL = 12.5,
                    monthlyFinancingInstallment = 1490.0,
                    monthlyMaintenanceCost = 350.0,
                    monthlyDepreciation = 780.0,
                    monthlyInsuranceAndTax = 360.0,
                    monthlyKmDriven = 4500.0,
                    calculatedCostPerKm = 1.11
                )
                val hb20 = VehicleCostProfile(
                    name = "Hyundai HB20",
                    vehicleType = "Quitado",
                    isSelectedForSemaforo = false,
                    fuelPricePerLiter = 5.89,
                    fuelConsumptionKmPerL = 13.2,
                    monthlyFinancingInstallment = 0.0,
                    monthlyMaintenanceCost = 280.0,
                    monthlyDepreciation = 420.0,
                    monthlyInsuranceAndTax = 260.0,
                    monthlyKmDriven = 4500.0,
                    calculatedCostPerKm = 0.64
                )
                db.vehicleDao().insertVehicle(onix)
                db.vehicleDao().insertVehicle(hb20)

                // Seed Settings
                db.settingsDao().insertOrUpdate(SemaforoSettings())

                // Seed Risk Keywords (Embarque, Destino, Mercados)
                val keywords = listOf(
                    RiskKeyword(word = "Beco dos Pardais", targetType = "EMBARQUE"),
                    RiskKeyword(word = "Vila Esperança", targetType = "EMBARQUE"),
                    RiskKeyword(word = "Comunidade da Serra", targetType = "DESTINO"),
                    RiskKeyword(word = "Rua Sem Saída Norte", targetType = "DESTINO"),
                    RiskKeyword(word = "Atacadão", targetType = "MERCADOS"),
                    RiskKeyword(word = "Assaí Atacadista", targetType = "MERCADOS"),
                    RiskKeyword(word = "Carrefour Hiper", targetType = "MERCADOS"),
                    RiskKeyword(word = "Supermercado BH", targetType = "MERCADOS")
                )
                keywords.forEach { db.riskKeywordDao().insertKeyword(it) }

                // Seed Sample History (as seen in screenshots!)
                val now = System.currentTimeMillis()
                val sampleRides = listOf(
                    RideOffer(
                        appName = "99",
                        grossAmount = 11.12,
                        pickupDistanceKm = 0.9,
                        pickupDurationMin = 5,
                        rideDistanceKm = 4.0,
                        rideDurationMin = 12,
                        totalDistanceKm = 4.9,
                        totalDurationMin = 17,
                        passengerName = "Gilmara",
                        passengerRating = 4.80,
                        originAddress = "Village do Lago, Montes Claros",
                        destinationAddress = "Ibituruna, Montes Claros",
                        status = "ACEITAR",
                        netProfit = 5.68,
                        profitMarginPercent = 51.0,
                        ratePerKm = 2.27,
                        ratePerHour = 39.25,
                        reasonsSummary = "R$/Km excelente • Margem positiva",
                        isAccepted = true,
                        timestamp = now - 3600000 * 2
                    ),
                    RideOffer(
                        appName = "Uber",
                        grossAmount = 28.00,
                        pickupDistanceKm = 2.1,
                        pickupDurationMin = 6,
                        rideDistanceKm = 10.8,
                        rideDurationMin = 19,
                        totalDistanceKm = 12.9,
                        totalDurationMin = 25,
                        passengerName = "Lucas Andrade",
                        passengerRating = 5.00,
                        originAddress = "Centro, Betim",
                        destinationAddress = "Jardim das Alterosas, Betim",
                        status = "ACEITAR",
                        netProfit = 13.68,
                        profitMarginPercent = 49.0,
                        ratePerKm = 2.17,
                        ratePerHour = 67.20,
                        reasonsSummary = "Excelente taxa por hora • Passageiro 5 estrelas",
                        isAccepted = true,
                        timestamp = now - 3600000 * 5
                    ),
                    RideOffer(
                        appName = "inDrive",
                        grossAmount = 14.50,
                        pickupDistanceKm = 1.5,
                        pickupDurationMin = 5,
                        rideDistanceKm = 6.2,
                        rideDurationMin = 16,
                        totalDistanceKm = 7.7,
                        totalDurationMin = 21,
                        passengerName = "Marcos Vinicius",
                        passengerRating = 4.75,
                        originAddress = "Shopping Boulevard",
                        destinationAddress = "Bairro Glória",
                        status = "ATENÇÃO",
                        netProfit = 5.95,
                        profitMarginPercent = 41.0,
                        ratePerKm = 1.88,
                        ratePerHour = 41.42,
                        reasonsSummary = "R$/Km na média • Avalie o trânsito",
                        isAccepted = false,
                        timestamp = now - 3600000 * 9
                    )
                )
                sampleRides.forEach { db.rideHistoryDao().insertRide(it) }

                // Seed sample dashcam recordings
                val recordings = listOf(
                    DashcamRecording(
                        title = "Viagem Betim - Centro",
                        durationFormatted = "12:34",
                        sizeFormatted = "410 MB",
                        cameraFacing = "Traseira",
                        quality = "1080p",
                        timestamp = now - 7200000
                    ),
                    DashcamRecording(
                        title = "Viagem Montes Claros",
                        durationFormatted = "08:51",
                        sizeFormatted = "290 MB",
                        cameraFacing = "Traseira",
                        quality = "1080p",
                        timestamp = now - 18000000
                    )
                )
                recordings.forEach { db.dashcamDao().insertRecording(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
