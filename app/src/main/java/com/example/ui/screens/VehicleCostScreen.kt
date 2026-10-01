package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VehicleCostProfile
import com.example.ui.MainViewModel
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald900
import com.example.ui.theme.SemaforoGreen
import com.example.ui.theme.SemaforoRed
import com.example.ui.theme.SemaforoYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VehicleCostScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val vehicles by viewModel.vehicles.collectAsState()
    val activeVehicle by viewModel.activeVehicle.collectAsState()

    var isAddingOrEditing by remember { mutableStateOf(false) }
    var editingVehicleId by remember { mutableStateOf(0L) }

    // Form fields
    var vehicleName by remember { mutableStateOf("Chevrolet Onix 1.0") }
    var vehicleType by remember { mutableStateOf("Financiado") } // Financiado, Alugado, Quitado
    var fuelPrice by remember { mutableDoubleStateOf(5.89) }
    var fuelConsumption by remember { mutableDoubleStateOf(12.5) }
    var financingInstallment by remember { mutableDoubleStateOf(1490.0) }
    var rentalRate by remember { mutableDoubleStateOf(2100.0) }
    var maintenanceCost by remember { mutableDoubleStateOf(350.0) }
    var depreciationCost by remember { mutableDoubleStateOf(780.0) }
    var insuranceTaxCost by remember { mutableDoubleStateOf(360.0) }
    var monthlyKm by remember { mutableDoubleStateOf(4500.0) }

    // Live calculation for preview
    val fuelMonthly = if (fuelConsumption > 0) (monthlyKm / fuelConsumption) * fuelPrice else 0.0
    val acquisitionMonthly = when (vehicleType) {
        "Financiado" -> financingInstallment
        "Alugado" -> rentalRate
        else -> 0.0
    }
    val totalMonthlyCost = fuelMonthly + acquisitionMonthly + maintenanceCost + depreciationCost + insuranceTaxCost
    val calculatedCostPerKm = if (monthlyKm > 0) totalMonthlyCost / monthlyKm else 1.0
    val survivalRate = calculatedCostPerKm * 1.55

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Screen Header
        Text(
            text = "Calculadora de Lucro Real",
            color = TextWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "Descubra quanto você realmente ganha depois de descontar cada centavo",
            color = TextMuted,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Step 3 (Screenshot 1): "Histórico de análises"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Histórico de análises",
                color = TextWhite,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = {
                    editingVehicleId = 0L
                    vehicleName = "Novo Veículo"
                    vehicleType = "Financiado"
                    isAddingOrEditing = !isAddingOrEditing
                },
                colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("button_add_vehicle")
            ) {
                Icon(
                    imageVector = if (isAddingOrEditing) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isAddingOrEditing) "Fechar" else "Novo Cálculo",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Vehicles List
        vehicles.forEach { vehicle ->
            val isActive = vehicle.isSelectedForSemaforo || activeVehicle?.id == vehicle.id
            val dateStr = SimpleDateFormat("dd/MM/yy", Locale("pt", "BR")).format(Date(vehicle.createdAt))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("vehicle_card_${vehicle.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = CardDefaults.outlinedCardBorder().copy(
                    width = if (isActive) 2.dp else 1.dp,
                    brush = Brush.linearGradient(
                        listOf(if (isActive) SemaforoGreen else DarkBorder, DarkBorder)
                    )
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (isActive) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF073826),
                            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(Emerald500, Emerald500))),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Emerald400, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "EM USO NO SEMÁFORO",
                                    color = Emerald400,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkCardElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsCar,
                                    contentDescription = null,
                                    tint = if (isActive) Emerald400 else TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = vehicle.name,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "$dateStr · ${vehicle.vehicleType}",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "R$ ${String.format(Locale.ROOT, "%.2f", vehicle.calculatedCostPerKm)}/km",
                                color = TextWhite,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Custo real",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isActive) {
                            Button(
                                onClick = {
                                    viewModel.setActiveVehicle(vehicle.id)
                                    Toast.makeText(context, "${vehicle.name} ativado no Semáforo!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("button_select_vehicle_${vehicle.id}")
                            ) {
                                Text("Ativar no Semáforo", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        IconButton(
                            onClick = {
                                editingVehicleId = vehicle.id
                                vehicleName = vehicle.name
                                vehicleType = vehicle.vehicleType
                                fuelPrice = vehicle.fuelPricePerLiter
                                fuelConsumption = vehicle.fuelConsumptionKmPerL
                                financingInstallment = vehicle.monthlyFinancingInstallment
                                rentalRate = vehicle.rentalRateMonthly
                                maintenanceCost = vehicle.monthlyMaintenanceCost
                                depreciationCost = vehicle.monthlyDepreciation
                                insuranceTaxCost = vehicle.monthlyInsuranceAndTax
                                monthlyKm = vehicle.monthlyKmDriven
                                isAddingOrEditing = true
                            },
                            modifier = Modifier.testTag("button_edit_vehicle_${vehicle.id}")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = TextMuted, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = {
                                viewModel.deleteVehicle(vehicle)
                                Toast.makeText(context, "Veículo excluído", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("button_delete_vehicle_${vehicle.id}")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = SemaforoRed.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Wizard Form (Screenshot 1: "Conte como é seu veículo" & "Veja o custo de verdade")
        AnimatedVisibility(visible = isAddingOrEditing) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_vehicle_wizard"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(Emerald500, DarkBorder)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (editingVehicleId == 0L) "Passo 1: Como é o seu veículo?" else "Editar Veículo",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Vehicle Name
                    OutlinedTextField(
                        value = vehicleName,
                        onValueChange = { vehicleName = it },
                        label = { Text("Nome do Veículo (ex: Onix 1.0, HB20)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_vehicle_name"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = Emerald400,
                            unfocusedBorderColor = DarkBorder
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 1 Options: Financiado / Alugado / Quitado (Screenshot 1)
                    Text("Modalidade de posse:", color = TextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    VehicleTypeOptionCard(
                        title = "Financiado",
                        subtitle = "Ainda pagando parcelas mensais",
                        isSelected = vehicleType == "Financiado",
                        onClick = { vehicleType = "Financiado" }
                    )
                    VehicleTypeOptionCard(
                        title = "Alugado",
                        subtitle = "Diária ou semanal (Locadora)",
                        isSelected = vehicleType == "Alugado",
                        onClick = { vehicleType = "Alugado" }
                    )
                    VehicleTypeOptionCard(
                        title = "Quitado",
                        subtitle = "Veículo próprio sem dívida",
                        isSelected = vehicleType == "Quitado",
                        onClick = { vehicleType = "Quitado" }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 2: "Veja o custo de verdade"
                    Text(
                        text = "Passo 2: Custos e Consumo",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Combustível
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = fuelPrice.toString(),
                            onValueChange = { fuelPrice = it.toDoubleOrNull() ?: fuelPrice },
                            label = { Text("Preço Combustível (R$/L)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                        OutlinedTextField(
                            value = fuelConsumption.toString(),
                            onValueChange = { fuelConsumption = it.toDoubleOrNull() ?: fuelConsumption },
                            label = { Text("Consumo Médio (km/l)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Parcela ou Aluguel
                    if (vehicleType == "Financiado") {
                        OutlinedTextField(
                            value = financingInstallment.toString(),
                            onValueChange = { financingInstallment = it.toDoubleOrNull() ?: financingInstallment },
                            label = { Text("Parcela Mensal do Financiamento (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                    } else if (vehicleType == "Alugado") {
                        OutlinedTextField(
                            value = rentalRate.toString(),
                            onValueChange = { rentalRate = it.toDoubleOrNull() ?: rentalRate },
                            label = { Text("Valor Total Mensal do Aluguel (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Manutenção e Depreciação
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = maintenanceCost.toString(),
                            onValueChange = { maintenanceCost = it.toDoubleOrNull() ?: maintenanceCost },
                            label = { Text("Manutenção/mês (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                        OutlinedTextField(
                            value = depreciationCost.toString(),
                            onValueChange = { depreciationCost = it.toDoubleOrNull() ?: depreciationCost },
                            label = { Text("Depreciação FIPE (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // IPVA/Seguro e Km rodados
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = insuranceTaxCost.toString(),
                            onValueChange = { insuranceTaxCost = it.toDoubleOrNull() ?: insuranceTaxCost },
                            label = { Text("IPVA + Seguro/mês (R$)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                        OutlinedTextField(
                            value = monthlyKm.toString(),
                            onValueChange = { monthlyKm = it.toDoubleOrNull() ?: monthlyKm },
                            label = { Text("Km rodados no mês") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Cost Breakdown Card (Screenshot 1: Step 2)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFF0C131D),
                        border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder))),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Custos mensais estimados", color = TextWhite, fontWeight = FontWeight.Bold)
                                Text(
                                    "R$ ${String.format(Locale.ROOT, "%.2f", totalMonthlyCost)}",
                                    color = Emerald400,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            CostShareRow(
                                icon = Icons.Default.LocalGasStation,
                                title = "Combustível",
                                amount = fuelMonthly,
                                perKm = if (monthlyKm > 0) fuelMonthly / monthlyKm else 0.0,
                                sharePercent = if (totalMonthlyCost > 0) (fuelMonthly / totalMonthlyCost) * 100 else 0.0
                            )
                            if (vehicleType != "Quitado") {
                                CostShareRow(
                                    icon = Icons.Default.Payments,
                                    title = if (vehicleType == "Financiado") "Financiamento" else "Aluguel",
                                    amount = acquisitionMonthly,
                                    perKm = if (monthlyKm > 0) acquisitionMonthly / monthlyKm else 0.0,
                                    sharePercent = if (totalMonthlyCost > 0) (acquisitionMonthly / totalMonthlyCost) * 100 else 0.0
                                )
                            }
                            CostShareRow(
                                icon = Icons.AutoMirrored.Filled.TrendingDown,
                                title = "Depreciação",
                                amount = depreciationCost,
                                perKm = if (monthlyKm > 0) depreciationCost / monthlyKm else 0.0,
                                sharePercent = if (totalMonthlyCost > 0) (depreciationCost / totalMonthlyCost) * 100 else 0.0
                            )
                            CostShareRow(
                                icon = Icons.Default.Build,
                                title = "Manutenção",
                                amount = maintenanceCost,
                                perKm = if (monthlyKm > 0) maintenanceCost / monthlyKm else 0.0,
                                sharePercent = if (totalMonthlyCost > 0) (maintenanceCost / totalMonthlyCost) * 100 else 0.0
                            )

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = DarkBorder)
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Seu Custo Real por Km:", color = TextMuted, fontSize = 12.sp)
                                    Text(
                                        "R$ ${String.format(Locale.ROOT, "%.2f", calculatedCostPerKm)}/km",
                                        color = TextWhite,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Tarifa de sobrevivência:", color = TextMuted, fontSize = 12.sp)
                                    Text(
                                        "R$ ${String.format(Locale.ROOT, "%.2f", survivalRate)}/km",
                                        color = SemaforoYellow,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save Button
                    Button(
                        onClick = {
                            viewModel.saveVehicle(
                                id = editingVehicleId,
                                name = vehicleName,
                                type = vehicleType,
                                fuelPrice = fuelPrice,
                                fuelConsumption = fuelConsumption,
                                financing = if (vehicleType == "Financiado") financingInstallment else 0.0,
                                rental = if (vehicleType == "Alugado") rentalRate else 0.0,
                                maintenance = maintenanceCost,
                                depreciation = depreciationCost,
                                insuranceTax = insuranceTaxCost,
                                monthlyKm = monthlyKm
                            )
                            isAddingOrEditing = false
                            Toast.makeText(context, "Análise do veículo salva com sucesso!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("button_save_vehicle")
                    ) {
                        Text(
                            text = "Salvar e Aplicar no Semáforo",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun VehicleTypeOptionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF0C2A1E) else DarkCardElevated,
        border = CardDefaults.outlinedCardBorder().copy(
            width = if (isSelected) 2.dp else 1.dp,
            brush = Brush.linearGradient(
                listOf(if (isSelected) SemaforoGreen else DarkBorder, DarkBorder)
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = if (isSelected) Emerald400 else TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Emerald400,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun CostShareRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    amount: Double,
    perKm: Double,
    sharePercent: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(title, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(
                    "R$ ${String.format(Locale.ROOT, "%.2f", perKm)}/km · ${String.format(Locale.ROOT, "%.0f", sharePercent)}%",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
        Text(
            "R$ ${String.format(Locale.ROOT, "%.2f", amount)}",
            color = TextWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
