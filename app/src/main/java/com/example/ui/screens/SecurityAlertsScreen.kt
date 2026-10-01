package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.model.RiskKeyword
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
import com.example.ui.theme.SemaforoRedBg
import com.example.ui.theme.SemaforoYellow
import com.example.ui.theme.SemaforoYellowBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SecurityAlertsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val settings by viewModel.settings.collectAsState()
    val riskKeywords by viewModel.riskKeywords.collectAsState()
    val recordings by viewModel.dashcamRecordings.collectAsState()

    var activeSubSection by remember { mutableIntStateOf(0) } // 0: Endereços de Risco, 1: Alertas da Viagem & Chat, 2: Câmera Secreta
    var selectedRiskTab by remember { mutableStateOf("EMBARQUE") } // EMBARQUE, DESTINO, MERCADOS
    var newKeywordInput by remember { mutableStateOf("") }

    var isRecordingSimulated by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Screen Header
        Text(
            text = "Segurança & Alertas",
            color = TextWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "Endereços perigosos, mercados, avisos de viagem e câmera testemunha",
            color = TextMuted,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Sub-tabs
        TabRow(
            selectedTabIndex = activeSubSection,
            containerColor = DarkCard,
            contentColor = Emerald400,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeSubSection]),
                    color = Emerald500
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = activeSubSection == 0,
                onClick = { activeSubSection = 0 },
                text = { Text("Endereços de Risco", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                selectedContentColor = Emerald400,
                unselectedContentColor = TextMuted
            )
            Tab(
                selected = activeSubSection == 1,
                onClick = { activeSubSection = 1 },
                text = { Text("Alertas & Chat", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                selectedContentColor = Emerald400,
                unselectedContentColor = TextMuted
            )
            Tab(
                selected = activeSubSection == 2,
                onClick = { activeSubSection = 2 },
                text = { Text("Câmera Secreta", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                selectedContentColor = Emerald400,
                unselectedContentColor = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeSubSection) {
            0 -> {
                // Section 0: Endereços de Risco (Screenshot 6)
                // Toggle: Alerta de endereço de risco
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SemaforoRedBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = SemaforoRed, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Alerta de endereço de risco", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        if (settings.riskAddressAlertEnabled) "Ativado" else "Desativado",
                                        color = if (settings.riskAddressAlertEnabled) Emerald400 else TextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Switch(
                                checked = settings.riskAddressAlertEnabled,
                                onCheckedChange = { viewModel.updateSettings(settings.copy(riskAddressAlertEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald400, checkedTrackColor = Emerald900),
                                modifier = Modifier.testTag("toggle_risk_address_alert")
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = DarkBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Toggle: Alerta para supermercados
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(SemaforoYellowBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = SemaforoYellow, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Alerta para supermercados", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        if (settings.supermarketAlertEnabled) "Ativado" else "Desativado",
                                        color = if (settings.supermarketAlertEnabled) Emerald400 else TextMuted,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Switch(
                                checked = settings.supermarketAlertEnabled,
                                onCheckedChange = { viewModel.updateSettings(settings.copy(supermarketAlertEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald400, checkedTrackColor = Emerald900),
                                modifier = Modifier.testTag("toggle_supermarket_alert")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Information banner (Screenshot 6)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF241A08),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(SemaforoYellow.copy(alpha = 0.5f), DarkBorder))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = SemaforoYellow, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "O alerta reconhece redes conhecidas (Mercado, Supermercado, Atacadão, Assaí). Mercadinhos locais com nome próprio podem passar despercebidos — cadastre-os aqui.",
                                color = Color(0xFFFDE68A),
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs: Embarque (count) | Destino (count) | Mercados (count) (Screenshot 6)
                val pickupKeywords = riskKeywords.filter { it.targetType == "EMBARQUE" }
                val destKeywords = riskKeywords.filter { it.targetType == "DESTINO" }
                val marketKeywords = riskKeywords.filter { it.targetType == "MERCADOS" }

                Text("PALAVRAS POR TIPO DE ENDEREÇO", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedRiskTab == "EMBARQUE",
                        onClick = { selectedRiskTab = "EMBARQUE" },
                        label = { Text("Embarque (${pickupKeywords.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald500,
                            selectedLabelColor = Color.Black,
                            containerColor = DarkCard,
                            labelColor = TextWhite
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedRiskTab == "DESTINO",
                        onClick = { selectedRiskTab = "DESTINO" },
                        label = { Text("Destino (${destKeywords.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald500,
                            selectedLabelColor = Color.Black,
                            containerColor = DarkCard,
                            labelColor = TextWhite
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedRiskTab == "MERCADOS",
                        onClick = { selectedRiskTab = "MERCADOS" },
                        label = { Text("Mercados (${marketKeywords.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Emerald500,
                            selectedLabelColor = Color.Black,
                            containerColor = DarkCard,
                            labelColor = TextWhite
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Add Keyword Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newKeywordInput,
                        onValueChange = { newKeywordInput = it },
                        placeholder = { Text("Ex: Beco do Zé, Rua Tal, Mercado X...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_risk_keyword"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = Emerald400,
                            unfocusedBorderColor = DarkBorder
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newKeywordInput.isNotBlank()) {
                                viewModel.addRiskKeyword(newKeywordInput, selectedRiskTab)
                                newKeywordInput = ""
                                Toast.makeText(context, "Palavra adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .testTag("button_add_risk_keyword")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Keyword Tags Flow
                val currentTabKeywords = when (selectedRiskTab) {
                    "EMBARQUE" -> pickupKeywords
                    "DESTINO" -> destKeywords
                    else -> marketKeywords
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentTabKeywords.forEach { kw ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkCardElevated,
                            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(kw.word, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remover",
                                    tint = SemaforoRed,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { viewModel.deleteRiskKeyword(kw) }
                                )
                            }
                        }
                    }
                }
            }

            1 -> {
                // Section 1: Alertas da Viagem & Mensagem Personalizada (Screenshot 3)
                // Alertas da viagem
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Alertas da viagem", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Usuário iniciante
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0C3831)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Emerald400, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Usuário iniciante", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Selo de passageiro novo nos 3 apps", color = TextMuted, fontSize = 12.sp)
                                }
                            }
                            Switch(
                                checked = settings.newPassengerBadgeEnabled,
                                onCheckedChange = { viewModel.updateSettings(settings.copy(newPassengerBadgeEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald400, checkedTrackColor = Emerald900)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = DarkBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Corridas com parada
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF281338)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.DirectionsRun, contentDescription = null, tint = Color(0xFFC084FC), modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Corridas com parada", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Aviso imediato no cartão antes de aceitar", color = TextMuted, fontSize = 12.sp)
                                }
                            }
                            Switch(
                                checked = settings.stopsAlertEnabled,
                                onCheckedChange = { viewModel.updateSettings(settings.copy(stopsAlertEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald400, checkedTrackColor = Emerald900)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mensagem personalizada (Screenshot 3)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF163228)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Emerald400, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Copiar ao abrir o chat", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Pronta pra colar na Uber, 99 e inDrive", color = TextMuted, fontSize = 12.sp)
                                }
                            }
                            Switch(
                                checked = settings.autoCopyChatEnabled,
                                onCheckedChange = { viewModel.updateSettings(settings.copy(autoCopyChatEnabled = it)) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Emerald400, checkedTrackColor = Emerald900)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("PRÉVIA DA MENSAGEM", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        // Chat bubble preview (Screenshot 3)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1B3D2F),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = settings.chatTemplate.replace("{nome}", "Gilmara").replace("{tempo}", "4"),
                                color = TextWhite,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                modifier = Modifier.padding(14.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        var editingTemplate by remember { mutableStateOf(settings.chatTemplate) }
                        OutlinedTextField(
                            value = editingTemplate,
                            onValueChange = {
                                editingTemplate = it
                                viewModel.updateSettings(settings.copy(chatTemplate = it))
                            },
                            label = { Text("Personalizar modelo de mensagem") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                focusedBorderColor = Emerald400,
                                unfocusedBorderColor = DarkBorder
                            )
                        )
                        Text(
                            text = "Use as tags {nome} e {tempo} para preenchimento dinâmico automático.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            2 -> {
                // Section 2: Câmera Secreta / Dashcam (Screenshot 5)
                // "Câmera Secreta: sua testemunha em cada viagem"
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF131E2A),
                    border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Câmera Secreta", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("Prova em vídeo, 100% discreta em segundo plano", color = TextMuted, fontSize = 12.sp)
                            }

                            // Badge REC discreta (Screenshot 5)
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isRecordingSimulated) SemaforoRedBg else DarkCardElevated,
                                border = CardDefaults.outlinedCardBorder().copy(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(listOf(if (isRecordingSimulated) SemaforoRed else DarkBorder, DarkBorder))
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(if (isRecordingSimulated) SemaforoRed else TextMuted, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isRecordingSimulated) "REC · 100% discreta" else "OFF",
                                        color = if (isRecordingSimulated) SemaforoRed else TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Storage Progress (Screenshot 5: Espaço usado 2,1 GB de 8 GB reservados)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Espaço usado", color = TextMuted, fontSize = 12.sp)
                            Text("2,1 GB de 8 GB reservados", color = Emerald400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 2.1f / 8.0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Emerald500,
                            trackColor = DarkBorder
                        )
                        Text(
                            text = "Gravações antigas são apagadas sozinhas quando o espaço enche.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                isRecordingSimulated = !isRecordingSimulated
                                if (isRecordingSimulated) {
                                    Toast.makeText(context, "Gravação discreta iniciada", Toast.LENGTH_SHORT).show()
                                } else {
                                    val now = System.currentTimeMillis()
                                    viewModel.addDashcamRecording(
                                        title = "Gravação de Viagem",
                                        duration = "05:12",
                                        size = "180 MB",
                                        facing = "Traseira",
                                        quality = "1080p"
                                    )
                                    Toast.makeText(context, "Gravação concluída e salva localmente!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isRecordingSimulated) SemaforoRed else Emerald500
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (isRecordingSimulated) Icons.Default.VideocamOff else Icons.Default.Videocam,
                                contentDescription = null,
                                tint = if (isRecordingSimulated) Color.White else Color.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRecordingSimulated) "Interromper e Salvar" else "Iniciar Gravação Discreta",
                                color = if (isRecordingSimulated) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recordings list (Screenshot 5)
                Text("Gravações salvas", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(10.dp))

                recordings.forEach { rec ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkCard)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = DarkCardElevated,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Reproduzir", tint = Emerald400)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(rec.title, color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        "${rec.durationFormatted} · ${rec.cameraFacing} · ${rec.quality} · ${rec.sizeFormatted}",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            IconButton(onClick = { viewModel.deleteRecording(rec) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = SemaforoRed.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
