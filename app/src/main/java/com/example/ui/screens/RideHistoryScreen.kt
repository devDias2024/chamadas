package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.QueryBuilder
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RideOffer
import com.example.ui.MainViewModel
import com.example.ui.components.AppBadge
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.SemaforoGreen
import com.example.ui.theme.SemaforoRed
import com.example.ui.theme.SemaforoYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RideHistoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val rides by viewModel.rideHistory.collectAsState()

    var selectedMainTab by remember { mutableIntStateOf(0) } // 0: Histórico de Corridas, 1: Melhores Horários
    var selectedAppFilter by remember { mutableStateOf("TODOS") }
    var selectedBestHoursTab by remember { mutableIntStateOf(0) } // 0: Melhores, 1: Intermediários, 2: Piores

    val filteredRides = rides.filter { ride ->
        when (selectedAppFilter) {
            "Uber" -> ride.appName == "Uber"
            "99" -> ride.appName == "99"
            "inDrive" -> ride.appName == "inDrive"
            "Aceitas" -> ride.isAccepted
            "Recusadas" -> !ride.isAccepted
            else -> true
        }
    }

    val totalGrossToday = rides.filter { it.isAccepted }.sumOf { it.grossAmount }
    val totalNetProfitToday = rides.filter { it.isAccepted }.sumOf { it.netProfit }
    val acceptanceRate = if (rides.isNotEmpty()) {
        (rides.count { it.isAccepted }.toDouble() / rides.size) * 100
    } else 0.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Daily Summary Header (Screenshot 2: SÁB, 25 JUL - R$ 171,00)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = SimpleDateFormat("EEE, dd MMM", Locale("pt", "BR")).format(Date()).uppercase(Locale.ROOT),
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "R$ ${String.format(Locale.ROOT, "%.2f", totalGrossToday)}",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkCard,
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Lucro Real", color = TextMuted, fontSize = 10.sp)
                        Text(
                            "R$ ${String.format(Locale.ROOT, "%.2f", totalNetProfitToday)}",
                            color = Emerald400,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Taxa Aceite", color = TextMuted, fontSize = 10.sp)
                        Text(
                            "${String.format(Locale.ROOT, "%.0f", acceptanceRate)}%",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Tabs: Histórico de corridas | Melhores horários (Screenshot 2)
        TabRow(
            selectedTabIndex = selectedMainTab,
            containerColor = DarkCard,
            contentColor = Emerald400,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedMainTab]),
                    color = Emerald500
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedMainTab == 0,
                onClick = { selectedMainTab = 0 },
                text = { Text("Histórico de corridas", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                selectedContentColor = Emerald400,
                unselectedContentColor = TextMuted,
                modifier = Modifier.testTag("tab_history_rides")
            )
            Tab(
                selected = selectedMainTab == 1,
                onClick = { selectedMainTab = 1 },
                text = { Text("Melhores horários", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                selectedContentColor = Emerald400,
                unselectedContentColor = TextMuted,
                modifier = Modifier.testTag("tab_best_hours")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedMainTab == 0) {
            // Histórico de corridas view
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("TODOS", "Uber", "99", "inDrive", "Aceitas", "Recusadas").forEach { filter ->
                    FilterChip(
                        selected = selectedAppFilter == filter,
                        onClick = { selectedAppFilter = filter },
                        label = { Text(filter, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald500,
                            selectedLabelColor = Color.Black,
                            containerColor = DarkCard,
                            labelColor = TextMuted
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("filter_chip_$filter")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rides List matching Screenshot 2
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredRides.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkCard)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Nenhuma corrida encontrada", color = TextWhite, fontWeight = FontWeight.Bold)
                                Text("As corridas avaliadas pelo Semáforo aparecerão aqui", color = TextMuted, fontSize = 12.sp)
                            }
                        }
                    }
                }

                items(filteredRides, key = { it.id }) { ride ->
                    RideHistoryItemCard(
                        ride = ride,
                        onDelete = { viewModel.deleteRide(ride) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                val csv = buildString {
                                    append("App,Valor,Km,Min,R$/Km,R$/Hora,Status,Aceita,Embarque,Destino,Data\n")
                                    rides.forEach { r ->
                                        append("${r.appName},${r.grossAmount},${r.totalDistanceKm},${r.totalDurationMin},${r.ratePerKm},${r.ratePerHour},${r.status},${r.isAccepted},\"${r.originAddress}\",\"${r.destinationAddress}\",${r.timestamp}\n")
                                    }
                                }
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Rota Pro CSV", csv)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Histórico copiado como CSV!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCardElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = Emerald400, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Exportar CSV", color = TextWhite, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.clearRideHistory()
                                Toast.makeText(context, "Histórico limpo", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkCardElevated),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = SemaforoRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Limpar", color = SemaforoRed, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        } else {
            // "Melhores horários" View (Screenshot 2)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                // Subtabs: Melhores | Intermediários | Piores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedBestHoursTab == 0,
                        onClick = { selectedBestHoursTab = 0 },
                        label = { Text("Melhores (Ouro)", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald500,
                            selectedLabelColor = Color.Black,
                            containerColor = DarkCard,
                            labelColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedBestHoursTab == 1,
                        onClick = { selectedBestHoursTab = 1 },
                        label = { Text("Intermediários") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SemaforoYellow,
                            selectedLabelColor = Color.Black,
                            containerColor = DarkCard,
                            labelColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedBestHoursTab == 2,
                        onClick = { selectedBestHoursTab = 2 },
                        label = { Text("Piores") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SemaforoRed,
                            selectedLabelColor = Color.White,
                            containerColor = DarkCard,
                            labelColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "📅 Mostrando: Semana atual · todos os dias",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Golden hours list (Screenshot 2: 08-10h R$ 3,33/km, 20-22h R$ 3,04/km)
                val hoursList = when (selectedBestHoursTab) {
                    0 -> listOf(
                        Triple("08–10h", "R$ 3,33/km", "• Ótima · 14 solicitações"),
                        Triple("20–22h", "R$ 3,04/km", "• Ótima · 18 solicitações"),
                        Triple("17–19h", "R$ 2,95/km", "• Ótima · 22 solicitações"),
                        Triple("06–08h", "R$ 2,80/km", "• Ótima · 11 solicitações")
                    )
                    1 -> listOf(
                        Triple("11–13h", "R$ 2,15/km", "• Na média · 9 solicitações"),
                        Triple("14–16h", "R$ 2,05/km", "• Na média · 8 solicitações")
                    )
                    else -> listOf(
                        Triple("13–14h", "R$ 1,42/km", "• Evitar · Baixa demanda"),
                        Triple("02–05h", "R$ 1,35/km", "• Evitar · Risco elevado")
                    )
                }

                hoursList.forEachIndexed { index, item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (selectedBestHoursTab == 0) Color(0xFF0C3826) else if (selectedBestHoursTab == 1) Color(0xFF38290A) else Color(0xFF3B1515),
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${index + 1}",
                                            color = if (selectedBestHoursTab == 0) Emerald400 else if (selectedBestHoursTab == 1) SemaforoYellow else SemaforoRed,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(item.first, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(item.third, color = TextMuted, fontSize = 12.sp)
                                }
                            }

                            Text(
                                text = item.second,
                                color = if (selectedBestHoursTab == 0) Emerald400 else if (selectedBestHoursTab == 1) SemaforoYellow else SemaforoRed,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RideHistoryItemCard(
    ride: RideOffer,
    onDelete: () -> Unit
) {
    val statusColor = when (ride.status) {
        "ACEITAR" -> SemaforoGreen
        "ATENÇÃO" -> SemaforoYellow
        else -> SemaforoRed
    }

    val timeFormatted = SimpleDateFormat("HH:mm", Locale("pt", "BR")).format(Date(ride.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ride_history_card_${ride.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.dp,
            brush = Brush.linearGradient(listOf(statusColor.copy(alpha = 0.5f), DarkBorder))
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: App badge, Gross, Time, Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppBadge(appName = ride.appName)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "R$ ${String.format(Locale.ROOT, "%.2f", ride.grossAmount)} · $timeFormatted",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (ride.isAccepted) "✓ Viagem aceita" else "✕ Viagem recusada",
                            color = if (ride.isAccepted) Emerald400 else SemaforoRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(statusColor, statusColor)))
                ) {
                    Text(
                        text = "• ${if (ride.status == "ACEITAR") "Ótima" else if (ride.status == "ATENÇÃO") "Atenção" else "Recusar"}",
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3 Key Metrics Row (Screenshot 2: R$ 2,27 por km | R$ 39,25 por hora | ★ 4,80 nota)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkCardElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "R$ ${String.format(Locale.ROOT, "%.2f", ride.ratePerKm)}",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text("por km", color = TextMuted, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "R$ ${String.format(Locale.ROOT, "%.2f", ride.ratePerHour)}",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text("por hora", color = TextMuted, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "★ ${String.format(Locale.ROOT, "%.2f", ride.passengerRating)}",
                            color = SemaforoYellow,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text("nota", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Route addresses (Screenshot 2: Village do Lago -> Ibituruna)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0C131D))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(Emerald400, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(ride.originAddress, color = TextWhite, fontSize = 12.sp, maxLines = 1, modifier = Modifier.weight(1f))
                    Text("${ride.pickupDurationMin} min", color = TextMuted, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).background(statusColor, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(ride.destinationAddress, color = TextWhite, fontSize = 12.sp, maxLines = 1, modifier = Modifier.weight(1f))
                    Text("${ride.rideDurationMin} min", color = TextMuted, fontSize = 11.sp)
                }
            }
        }
    }
}
