package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.DashcamRecording
import com.example.data.model.RideOffer
import com.example.data.model.RiskKeyword
import com.example.data.model.SemaforoSettings
import com.example.data.model.VehicleCostProfile
import com.example.domain.EvaluationResult
import com.example.domain.SemaforoEngine
import com.example.domain.VoiceNotifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RideInputState(
    val app: String = "Uber",
    val gross: Double = 28.00,
    val pickupDist: Double = 2.1,
    val pickupDur: Int = 6,
    val rideDist: Double = 10.8,
    val rideDur: Int = 19,
    val rating: Double = 5.00,
    val passenger: String = "Lucas Andrade",
    val origin: String = "Centro, Betim",
    val dest: String = "Jardim Alterosas, Betim",
    val isNew: Boolean = false,
    val stops: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val vehicleDao = db.vehicleDao()
    private val settingsDao = db.settingsDao()
    private val rideHistoryDao = db.rideHistoryDao()
    private val riskKeywordDao = db.riskKeywordDao()
    private val dashcamDao = db.dashcamDao()

    private val voiceNotifier = VoiceNotifier(application)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AppDatabase.populateInitialData(db)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Database flows
    val vehicles: StateFlow<List<VehicleCostProfile>> = vehicleDao.getAllVehicles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeVehicle: StateFlow<VehicleCostProfile?> = vehicleDao.getActiveVehicle()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val settings: StateFlow<SemaforoSettings> = settingsDao.getSettings()
        .map { it ?: SemaforoSettings() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SemaforoSettings())

    val rideHistory: StateFlow<List<RideOffer>> = rideHistoryDao.getAllRides()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val riskKeywords: StateFlow<List<RiskKeyword>> = riskKeywordDao.getAllKeywords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dashcamRecordings: StateFlow<List<DashcamRecording>> = dashcamDao.getAllRecordings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Unified Type-Safe Ride Input State
    private val _rideInput = MutableStateFlow(RideInputState())
    val rideInput = _rideInput.asStateFlow()

    // Convenience property accessors for UI
    val currentApp: StateFlow<String> = _rideInput.map { it.app }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Uber")
    val grossAmount: StateFlow<Double> = _rideInput.map { it.gross }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 28.00)
    val pickupDistance: StateFlow<Double> = _rideInput.map { it.pickupDist }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2.1)
    val pickupDuration: StateFlow<Int> = _rideInput.map { it.pickupDur }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 6)
    val rideDistance: StateFlow<Double> = _rideInput.map { it.rideDist }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 10.8)
    val rideDuration: StateFlow<Int> = _rideInput.map { it.rideDur }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 19)
    val passengerName: StateFlow<String> = _rideInput.map { it.passenger }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Lucas Andrade")
    val passengerRating: StateFlow<Double> = _rideInput.map { it.rating }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5.00)
    val originAddress: StateFlow<String> = _rideInput.map { it.origin }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Centro, Betim")
    val destinationAddress: StateFlow<String> = _rideInput.map { it.dest }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Jardim Alterosas, Betim")
    val isNewPassenger: StateFlow<Boolean> = _rideInput.map { it.isNew }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val hasStops: StateFlow<Boolean> = _rideInput.map { it.stops }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Preview filter mode: "TODOS", "ACEITAR", "ATENÇÃO", "RECUSAR"
    private val _previewFilter = MutableStateFlow("TODOS")
    val previewFilter = _previewFilter.asStateFlow()

    // Card style type: "COMPLETO" or "SECUNDARIO"
    private val _cardLayoutType = MutableStateFlow("COMPLETO")
    val cardLayoutType = _cardLayoutType.asStateFlow()

    // 100% Type-Safe Evaluation Flow
    val currentEvaluation: StateFlow<EvaluationResult> = combine(
        activeVehicle, settings, riskKeywords, _rideInput
    ) { v, s, rk, input ->
        SemaforoEngine.evaluateRide(
            appName = input.app,
            grossAmount = input.gross,
            pickupDistanceKm = input.pickupDist,
            pickupDurationMin = input.pickupDur,
            rideDistanceKm = input.rideDist,
            rideDurationMin = input.rideDur,
            passengerRating = input.rating,
            passengerName = input.passenger,
            originAddress = input.origin,
            destinationAddress = input.dest,
            isNewPassenger = input.isNew,
            hasStops = input.stops,
            vehicle = v,
            settings = s,
            riskKeywords = rk
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        EvaluationResult("ACEITAR", 13.68, 49.0, 2.17, 67.2, 12.9, 25, listOf("Excelente taxa por km"), false, false, false, false)
    )

    // Online Journey tracker state
    private val _isOnline = MutableStateFlow(true)
    val isOnline = _isOnline.asStateFlow()

    private val _onlineMinutes = MutableStateFlow(185)
    val onlineMinutes = _onlineMinutes.asStateFlow()

    fun toggleOnline() {
        _isOnline.value = !_isOnline.value
    }

    fun setPreviewFilter(filter: String) {
        _previewFilter.value = filter
        when (filter) {
            "ACEITAR" -> loadAcceptPreset()
            "ATENÇÃO" -> loadCautionPreset()
            "RECUSAR" -> loadDeclinePreset()
        }
    }

    fun setCardLayoutType(type: String) {
        _cardLayoutType.value = type
        viewModelScope.launch {
            try {
                val curr = settings.value
                settingsDao.insertOrUpdate(curr.copy(cardLayoutType = type))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadAcceptPreset() {
        _rideInput.value = RideInputState(
            app = "Uber",
            gross = 28.00,
            pickupDist = 2.1,
            pickupDur = 6,
            rideDist = 10.8,
            rideDur = 19,
            passenger = "Lucas Andrade",
            rating = 5.00,
            origin = "Centro, Betim",
            dest = "Jardim Alterosas, Betim",
            isNew = false,
            stops = false
        )
    }

    fun loadCautionPreset() {
        _rideInput.value = RideInputState(
            app = "inDrive",
            gross = 14.50,
            pickupDist = 1.5,
            pickupDur = 5,
            rideDist = 6.2,
            rideDur = 16,
            passenger = "Marcos Vinicius",
            rating = 4.75,
            origin = "Shopping Boulevard",
            dest = "Bairro Glória",
            isNew = false,
            stops = true
        )
    }

    fun loadDeclinePreset() {
        _rideInput.value = RideInputState(
            app = "99",
            gross = 7.50,
            pickupDist = 4.2,
            pickupDur = 12,
            rideDist = 2.8,
            rideDur = 8,
            passenger = "Usuário Anônimo",
            rating = 4.52,
            origin = "Atacadão Rodovia",
            dest = "Vila Esperança",
            isNew = true,
            stops = false
        )
    }

    fun updateOfferManual(
        appName: String,
        amount: Double,
        pickupDist: Double,
        pickupDur: Int,
        rideDist: Double,
        rideDur: Int,
        rating: Double,
        passenger: String,
        orig: String,
        dest: String,
        isNew: Boolean,
        stops: Boolean
    ) {
        _rideInput.value = RideInputState(
            app = appName,
            gross = amount,
            pickupDist = pickupDist,
            pickupDur = pickupDur,
            rideDist = rideDist,
            rideDur = rideDur,
            rating = rating,
            passenger = passenger,
            origin = orig,
            dest = dest,
            isNew = isNew,
            stops = stops
        )
    }

    fun speakCurrentEvaluation() {
        val eval = currentEvaluation.value
        voiceNotifier.speakEvaluation(eval.status, eval.ratePerKm, eval.netProfit)
    }

    fun acceptCurrentRide() {
        saveRideToHistory(accepted = true)
        speakCurrentEvaluation()
    }

    fun declineCurrentRide() {
        saveRideToHistory(accepted = false)
    }

    private fun saveRideToHistory(accepted: Boolean) {
        viewModelScope.launch {
            try {
                val eval = currentEvaluation.value
                val input = _rideInput.value
                val offer = RideOffer(
                    appName = input.app,
                    grossAmount = input.gross,
                    pickupDistanceKm = input.pickupDist,
                    pickupDurationMin = input.pickupDur,
                    rideDistanceKm = input.rideDist,
                    rideDurationMin = input.rideDur,
                    totalDistanceKm = eval.totalDistanceKm,
                    totalDurationMin = eval.totalDurationMin,
                    passengerName = input.passenger,
                    passengerRating = input.rating,
                    originAddress = input.origin,
                    destinationAddress = input.dest,
                    isNewPassenger = input.isNew,
                    hasStops = input.stops,
                    status = eval.status,
                    netProfit = eval.netProfit,
                    profitMarginPercent = eval.profitMarginPercent,
                    ratePerKm = eval.ratePerKm,
                    ratePerHour = eval.ratePerHour,
                    reasonsSummary = eval.reasons.joinToString(" • "),
                    isAccepted = accepted,
                    timestamp = System.currentTimeMillis()
                )
                rideHistoryDao.insertRide(offer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setActiveVehicle(id: Long) {
        viewModelScope.launch {
            try {
                vehicleDao.setActiveVehicle(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveVehicle(
        id: Long = 0,
        name: String,
        type: String,
        fuelPrice: Double,
        fuelConsumption: Double,
        financing: Double,
        rental: Double,
        maintenance: Double,
        depreciation: Double,
        insuranceTax: Double,
        monthlyKm: Double
    ) {
        viewModelScope.launch {
            try {
                val temp = VehicleCostProfile(
                    id = id,
                    name = name,
                    vehicleType = type,
                    isSelectedForSemaforo = id == 0L && (vehicles.value.isEmpty()),
                    fuelPricePerLiter = fuelPrice,
                    fuelConsumptionKmPerL = fuelConsumption,
                    monthlyFinancingInstallment = financing,
                    rentalRateMonthly = rental,
                    monthlyMaintenanceCost = maintenance,
                    monthlyDepreciation = depreciation,
                    monthlyInsuranceAndTax = insuranceTax,
                    monthlyKmDriven = monthlyKm
                )
                val costPerKm = temp.calculateCostPerKm()
                val vehicle = temp.copy(calculatedCostPerKm = costPerKm)
                val newId = vehicleDao.insertVehicle(vehicle)
                if (vehicles.value.isEmpty()) {
                    vehicleDao.setActiveVehicle(newId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteVehicle(vehicle: VehicleCostProfile) {
        viewModelScope.launch {
            try {
                vehicleDao.deleteVehicle(vehicle)
                val remaining = vehicleDao.getActiveVehicleSync()
                if (remaining == null && vehicles.value.isNotEmpty()) {
                    val first = vehicles.value.firstOrNull { it.id != vehicle.id }
                    if (first != null) {
                        vehicleDao.setActiveVehicle(first.id)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateSettings(newSettings: SemaforoSettings) {
        viewModelScope.launch {
            try {
                settingsDao.insertOrUpdate(newSettings)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addRiskKeyword(word: String, targetType: String) {
        if (word.isBlank()) return
        viewModelScope.launch {
            try {
                riskKeywordDao.insertKeyword(RiskKeyword(word = word.trim(), targetType = targetType))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteRiskKeyword(keyword: RiskKeyword) {
        viewModelScope.launch {
            try {
                riskKeywordDao.deleteKeyword(keyword)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun clearRideHistory() {
        viewModelScope.launch {
            try {
                rideHistoryDao.clearAll()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteRide(ride: RideOffer) {
        viewModelScope.launch {
            try {
                rideHistoryDao.deleteRide(ride)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addDashcamRecording(title: String, duration: String, size: String, facing: String, quality: String) {
        viewModelScope.launch {
            try {
                dashcamDao.insertRecording(
                    DashcamRecording(
                        title = title,
                        durationFormatted = duration,
                        sizeFormatted = size,
                        cameraFacing = facing,
                        quality = quality
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteRecording(recording: DashcamRecording) {
        viewModelScope.launch {
            try {
                dashcamDao.deleteRecording(recording)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceNotifier.shutdown()
    }
}
