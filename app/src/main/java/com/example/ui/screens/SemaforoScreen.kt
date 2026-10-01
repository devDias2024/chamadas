package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.service.FloatingOverlayService
import com.example.ui.MainViewModel
import com.example.ui.components.SemaforoCard
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald900
import com.example.ui.theme.SemaforoGreen
import com.example.ui.theme.SemaforoRed
import com.example.ui.theme.SemaforoYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SemaforoScreen(
    viewModel: MainViewModel,
    onNavigateToVehicles: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val currentApp by viewModel.currentApp.collectAsState()
    val grossAmount by viewModel.grossAmount.collectAsState()
    val pickupDistance by viewModel.pickupDistance.collectAsState()
    val pickupDuration by viewModel.pickupDuration.collectAsState()
    val rideDistance by viewModel.rideDistance.collectAsState()
    val rideDuration by viewModel.rideDuration.collectAsState()
    val passengerName by viewModel.passengerName.collectAsState()
    val passengerRating by viewModel.passengerRating.collectAsState()
    val originAddress by viewModel.originAddress.collectAsState()
    val destinationAddress by viewModel.destinationAddress.collectAsState()
    val isNewPassenger by viewModel.isNewPassenger.collectAsState()
    val hasStops by viewModel.hasStops.collectAsState()

    val evaluation by viewModel.currentEvaluation.collectAsState()
    val activeVehicle by viewModel.activeVehicle.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val previewFilter by viewModel.previewFilter.collectAsState()
    val cardLayoutType by viewModel.cardLayoutType.collectAsState()

    val isOnline by viewModel.isOnline.collectAsState()
    val onlineMinutes by viewModel.onlineMinutes.collectAsState()

    var showManualEditor by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Header: Active Vehicle & Jornada Online Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Semáforo de Valores",
                    color = TextWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Saiba na hora se a corrida vale a pena",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }

            // Online Journey Pill (Screenshot 4)
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = if (isOnline) Color(0xFF063A29) else DarkCardElevated,
                border = CardDefaults.outlinedCardBorder().copy(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(if (isOnline) SemaforoGreen else TextMuted, DarkBorder)
                    )
                ),
                modifier = Modifier
                    .clickable { viewModel.toggleOnline() }
                    .testTag("toggle_online_status")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(if (isOnline) SemaforoGreen else TextMuted, CircleShape)
                    )
                    Text(
                        text = if (isOnline) "${onlineMinutes / 60}h ${onlineMinutes % 60}m Online" else "Offline",
                        color = if (isOnline) Emerald400 else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Active Vehicle Banner (Shows active cost per km)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkCard,
            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder))),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToVehicles() }
                .testTag("active_vehicle_banner")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(DarkCardElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Emerald400,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeVehicle?.name ?: "Nenhum veículo selecionado",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Emerald900
                            ) {
                                Text(
                                    text = "EM USO",
                                    color = Emerald400,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Custo base: R$ ${String.format(Locale.ROOT, "%.2f", activeVehicle?.calculatedCostPerKm ?: 1.11)}/km (${activeVehicle?.vehicleType ?: "Configurar"})",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Text(
                    text = "Alterar ›",
                    color = Emerald400,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "Ver a prévia como:" (Screenshot 2: • Aceitar | • Atenção | • Recusar)
        Text(
            text = "Ver a prévia como:",
            color = TextMuted,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = previewFilter == "ACEITAR",
                onClick = { viewModel.setPreviewFilter("ACEITAR") },
                label = { Text("• Aceitar (Verde)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SemaforoGreen,
                    selectedLabelColor = Color.Black,
                    containerColor = DarkCard,
                    labelColor = TextWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("preview_filter_accept")
            )
            FilterChip(
                selected = previewFilter == "ATENÇÃO",
                onClick = { viewModel.setPreviewFilter("ATENÇÃO") },
                label = { Text("• Atenção (Amarelo)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SemaforoYellow,
                    selectedLabelColor = Color.Black,
                    containerColor = DarkCard,
                    labelColor = TextWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("preview_filter_caution")
            )
            FilterChip(
                selected = previewFilter == "RECUSAR",
                onClick = { viewModel.setPreviewFilter("RECUSAR") },
                label = { Text("• Recusar (Vermelho)") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SemaforoRed,
                    selectedLabelColor = Color.White,
                    containerColor = DarkCard,
                    labelColor = TextWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("preview_filter_decline")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // "TIPO DE CARTÃO" (Screenshot 2: Completo | Secundário)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TIPO DE CARTÃO",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (cardLayoutType == "COMPLETO") Emerald500 else DarkCard,
                    modifier = Modifier
                        .clickable { viewModel.setCardLayoutType("COMPLETO") }
                        .testTag("card_type_complete")
                ) {
                    Text(
                        text = "Completo",
                        color = if (cardLayoutType == "COMPLETO") Color.Black else TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (cardLayoutType == "SECUNDARIO") Emerald500 else DarkCard,
                    modifier = Modifier
                        .clickable { viewModel.setCardLayoutType("SECUNDARIO") }
                        .testTag("card_type_secondary")
                ) {
                    Text(
                        text = "Secundário",
                        color = if (cardLayoutType == "SECUNDARIO") Color.Black else TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // THE SEMÁFORO CARD (The Hero Component!)
        SemaforoCard(
            appName = currentApp,
            grossAmount = grossAmount,
            pickupDistanceKm = pickupDistance,
            pickupDurationMin = pickupDuration,
            rideDistanceKm = rideDistance,
            rideDurationMin = rideDuration,
            passengerName = passengerName,
            passengerRating = passengerRating,
            originAddress = originAddress,
            destinationAddress = destinationAddress,
            isNewPassenger = isNewPassenger,
            hasStops = hasStops,
            evaluation = evaluation,
            chatTemplate = settings.chatTemplate,
            isSecondaryLayout = cardLayoutType == "SECUNDARIO",
            onSpeak = { viewModel.speakCurrentEvaluation() },
            onAccept = {
                viewModel.acceptCurrentRide()
                Toast.makeText(context, "Corrida aceita e salva no histórico!", Toast.LENGTH_SHORT).show()
            },
            onDecline = {
                viewModel.declineCurrentRide()
                Toast.makeText(context, "Corrida recusada salva no histórico!", Toast.LENGTH_SHORT).show()
            }
        )

        // Reasons summary breakdown
        if (evaluation.reasons.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Veredito detalhado do Semáforo:",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    evaluation.reasons.forEach { reason ->
                        Text(
                            text = "• $reason",
                            color = if (evaluation.status == "RECUSAR") SemaforoRed else if (evaluation.status == "ATENÇÃO") SemaforoYellow else Emerald400,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Overlay & Floating HUD Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Semáforo Flutuante sobre Apps",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Aparece por cima da Uber, 99 e inDrive sem atrapalhar",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:" + context.packageName)
                                )
                                context.startActivity(intent)
                                Toast.makeText(context, "Ative a permissão de sobreposição para o Rota Pro", Toast.LENGTH_LONG).show()
                            } else {
                                if (FloatingOverlayService.isRunning) {
                                    val stopIntent = Intent(context, FloatingOverlayService::class.java)
                                    context.stopService(stopIntent)
                                    Toast.makeText(context, "Semáforo flutuante desativado", Toast.LENGTH_SHORT).show()
                                } else {
                                    val startIntent = Intent(context, FloatingOverlayService::class.java)
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        context.startForegroundService(startIntent)
                                    } else {
                                        context.startService(startIntent)
                                    }
                                    Toast.makeText(context, "Semáforo flutuante ativado!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (FloatingOverlayService.isRunning) SemaforoRed else Emerald500
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("button_toggle_overlay_service")
                    ) {
                        Text(
                            text = if (FloatingOverlayService.isRunning) "Fechar HUD" else "Testar HUD",
                            color = if (FloatingOverlayService.isRunning) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Manual Ride Simulator Drawer / Custom Offer Tester
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkCardElevated,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showManualEditor = !showManualEditor }
                .testTag("toggle_simulator_drawer")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = Emerald400,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Simular Corrida Personalizada",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Text(
                    text = if (showManualEditor) "Ocultar ▲" else "Configurar ▼",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        AnimatedVisibility(visible = showManualEditor) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkCard)
                    .padding(16.dp)
            ) {
                // App selection
                Text("Aplicativo:", color = TextMuted, fontSize = 12.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Uber", "99", "inDrive").forEach { app ->
                        FilterChip(
                            selected = currentApp == app,
                            onClick = {
                                viewModel.updateOfferManual(
                                    appName = app, grossAmount, pickupDistance, pickupDuration,
                                    rideDistance, rideDuration, passengerRating, passengerName,
                                    originAddress, destinationAddress, isNewPassenger, hasStops
                                )
                            },
                            label = { Text(app) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Emerald500,
                                selectedLabelColor = Color.Black,
                                containerColor = DarkCardElevated,
                                labelColor = TextWhite
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Gross Amount input
                OutlinedTextField(
                    value = grossAmount.toString(),
                    onValueChange = {
                        val num = it.toDoubleOrNull() ?: grossAmount
                        viewModel.updateOfferManual(
                            currentApp, num, pickupDistance, pickupDuration,
                            rideDistance, rideDuration, passengerRating, passengerName,
                            originAddress, destinationAddress, isNewPassenger, hasStops
                        )
                    },
                    label = { Text("Valor Bruto da Oferta (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_gross_amount"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = Emerald400,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pickupDistance.toString(),
                        onValueChange = {
                            val num = it.toDoubleOrNull() ?: pickupDistance
                            viewModel.updateOfferManual(
                                currentApp, grossAmount, num, pickupDuration,
                                rideDistance, rideDuration, passengerRating, passengerName,
                                originAddress, destinationAddress, isNewPassenger, hasStops
                            )
                        },
                        label = { Text("Dist. Embarque (km)") },
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
                        value = rideDistance.toString(),
                        onValueChange = {
                            val num = it.toDoubleOrNull() ?: rideDistance
                            viewModel.updateOfferManual(
                                currentApp, grossAmount, pickupDistance, pickupDuration,
                                num, rideDuration, passengerRating, passengerName,
                                originAddress, destinationAddress, isNewPassenger, hasStops
                            )
                        },
                        label = { Text("Dist. Corrida (km)") },
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

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pickupDuration.toString(),
                        onValueChange = {
                            val num = it.toIntOrNull() ?: pickupDuration
                            viewModel.updateOfferManual(
                                currentApp, grossAmount, pickupDistance, num,
                                rideDistance, rideDuration, passengerRating, passengerName,
                                originAddress, destinationAddress, isNewPassenger, hasStops
                            )
                        },
                        label = { Text("Tempo Embarque (min)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = Emerald400,
                            unfocusedBorderColor = DarkBorder
                        )
                    )
                    OutlinedTextField(
                        value = rideDuration.toString(),
                        onValueChange = {
                            val num = it.toIntOrNull() ?: rideDuration
                            viewModel.updateOfferManual(
                                currentApp, grossAmount, pickupDistance, pickupDuration,
                                rideDistance, num, passengerRating, passengerName,
                                originAddress, destinationAddress, isNewPassenger, hasStops
                            )
                        },
                        label = { Text("Tempo Viagem (min)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                OutlinedTextField(
                    value = passengerRating.toString(),
                    onValueChange = {
                        val num = it.toDoubleOrNull() ?: passengerRating
                        viewModel.updateOfferManual(
                            currentApp, grossAmount, pickupDistance, pickupDuration,
                            rideDistance, rideDuration, num, passengerName,
                            originAddress, destinationAddress, isNewPassenger, hasStops
                        )
                    },
                    label = { Text("Nota do Passageiro (★)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = Emerald400,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = originAddress,
                    onValueChange = {
                        viewModel.updateOfferManual(
                            currentApp, grossAmount, pickupDistance, pickupDuration,
                            rideDistance, rideDuration, passengerRating, passengerName,
                            it, destinationAddress, isNewPassenger, hasStops
                        )
                    },
                    label = { Text("Endereço de Embarque") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = Emerald400,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = destinationAddress,
                    onValueChange = {
                        viewModel.updateOfferManual(
                            currentApp, grossAmount, pickupDistance, pickupDuration,
                            rideDistance, rideDuration, passengerRating, passengerName,
                            originAddress, it, isNewPassenger, hasStops
                        )
                    },
                    label = { Text("Endereço de Destino") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = Emerald400,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Usuário Iniciante", color = TextWhite, fontSize = 13.sp)
                    Switch(
                        checked = isNewPassenger,
                        onCheckedChange = {
                            viewModel.updateOfferManual(
                                currentApp, grossAmount, pickupDistance, pickupDuration,
                                rideDistance, rideDuration, passengerRating, passengerName,
                                originAddress, destinationAddress, it, hasStops
                            )
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Emerald400, checkedTrackColor = Emerald900)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Corrida com Múltiplas Paradas", color = TextWhite, fontSize = 13.sp)
                    Switch(
                        checked = hasStops,
                        onCheckedChange = {
                            viewModel.updateOfferManual(
                                currentApp, grossAmount, pickupDistance, pickupDuration,
                                rideDistance, rideDuration, passengerRating, passengerName,
                                originAddress, destinationAddress, isNewPassenger, it
                            )
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Emerald400, checkedTrackColor = Emerald900)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
