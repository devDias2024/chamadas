package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
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
import java.util.Locale

@Composable
fun LimitsSettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val settings by viewModel.settings.collectAsState()

    var minRatePerKmGreen by remember(settings) { mutableDoubleStateOf(settings.minRatePerKmGreen) }
    var minRatePerKmYellow by remember(settings) { mutableDoubleStateOf(settings.minRatePerKmYellow) }
    var minRatePerHourGreen by remember(settings) { mutableDoubleStateOf(settings.minRatePerHourGreen) }
    var minRatePerHourYellow by remember(settings) { mutableDoubleStateOf(settings.minRatePerHourYellow) }
    var minPassengerRating by remember(settings) { mutableDoubleStateOf(settings.minPassengerRating) }
    var minRideValue by remember(settings) { mutableDoubleStateOf(settings.minRideValue) }
    var minProfitValue by remember(settings) { mutableDoubleStateOf(settings.minProfitValue) }
    var minProfitPercent by remember(settings) { mutableDoubleStateOf(settings.minProfitPercent) }
    var maxPickupDistanceKm by remember(settings) { mutableDoubleStateOf(settings.maxPickupDistanceKm) }
    var maxPickupTimeMinutes by remember(settings) { mutableIntStateOf(settings.maxPickupTimeMinutes) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Screen Header
        Text(
            text = "Seus Limites, Suas Regras",
            color = TextWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "Você define o que é corrida boa. Arraste os controles para ajustar as faixas do Semáforo.",
            color = TextMuted,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Faixas de R$/Km
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = Emerald400, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Faixas de R$/Km", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Green Threshold
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Verde (Aceitar com lucro):", color = SemaforoGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("Acima de R$ ${String.format(Locale.ROOT, "%.2f", minRatePerKmGreen)}/km", color = TextWhite, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = minRatePerKmGreen.toFloat(),
                    onValueChange = { minRatePerKmGreen = (Math.round(it * 10.0) / 10.0).coerceAtLeast(minRatePerKmYellow + 0.1) },
                    valueRange = 1.5f..4.5f,
                    colors = SliderDefaults.colors(thumbColor = SemaforoGreen, activeTrackColor = SemaforoGreen),
                    modifier = Modifier.testTag("slider_rate_km_green")
                )

                // Yellow Threshold
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Amarelo (Atenção / Na média):", color = SemaforoYellow, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("Entre R$ ${String.format(Locale.ROOT, "%.2f", minRatePerKmYellow)} e R$ ${String.format(Locale.ROOT, "%.2f", minRatePerKmGreen)}", color = TextWhite, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = minRatePerKmYellow.toFloat(),
                    onValueChange = { minRatePerKmYellow = (Math.round(it * 10.0) / 10.0).coerceAtMost(minRatePerKmGreen - 0.1) },
                    valueRange = 1.0f..3.0f,
                    colors = SliderDefaults.colors(thumbColor = SemaforoYellow, activeTrackColor = SemaforoYellow),
                    modifier = Modifier.testTag("slider_rate_km_yellow")
                )

                Text(
                    text = "Abaixo de R$ ${String.format(Locale.ROOT, "%.2f", minRatePerKmYellow)}/km acende Vermelho (Prejuízo) imediatamente.",
                    color = SemaforoRed,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Faixas de R$/Hora
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = Emerald400, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Faixas de R$/Hora", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Verde (Meta por hora):", color = SemaforoGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("Acima de R$ ${String.format(Locale.ROOT, "%.0f", minRatePerHourGreen)}/h", color = TextWhite, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = minRatePerHourGreen.toFloat(),
                    onValueChange = { minRatePerHourGreen = Math.round(it).toDouble().coerceAtLeast(minRatePerHourYellow + 5.0) },
                    valueRange = 30f..100f,
                    colors = SliderDefaults.colors(thumbColor = SemaforoGreen, activeTrackColor = SemaforoGreen)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Amarelo (Média):", color = SemaforoYellow, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("Acima de R$ ${String.format(Locale.ROOT, "%.0f", minRatePerHourYellow)}/h", color = TextWhite, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = minRatePerHourYellow.toFloat(),
                    onValueChange = { minRatePerHourYellow = Math.round(it).toDouble().coerceAtMost(minRatePerHourGreen - 5.0) },
                    valueRange = 20f..70f,
                    colors = SliderDefaults.colors(thumbColor = SemaforoYellow, activeTrackColor = SemaforoYellow)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Nota do Passageiro e Margem de Lucro
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = SemaforoYellow, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nota Mínima do Passageiro", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Avaliação mínima aceita:", color = TextMuted, fontSize = 13.sp)
                    Text("★ ${String.format(Locale.ROOT, "%.2f", minPassengerRating)}", color = Emerald400, fontWeight = FontWeight.Black)
                }
                Slider(
                    value = minPassengerRating.toFloat(),
                    onValueChange = { minPassengerRating = Math.round(it * 100.0) / 100.0 },
                    valueRange = 4.40f..4.95f,
                    colors = SliderDefaults.colors(thumbColor = Emerald500, activeTrackColor = Emerald500)
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = DarkBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Percent, contentDescription = null, tint = Emerald400, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Margem de Lucro Mínima (%)", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Meta de lucro líquido:", color = TextMuted, fontSize = 13.sp)
                    Text("${String.format(Locale.ROOT, "%.0f", minProfitPercent)}%", color = Emerald400, fontWeight = FontWeight.Black)
                }
                Slider(
                    value = minProfitPercent.toFloat(),
                    onValueChange = { minProfitPercent = Math.round(it).toDouble() },
                    valueRange = 20f..70f,
                    colors = SliderDefaults.colors(thumbColor = Emerald500, activeTrackColor = Emerald500)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Limites de Embarque (Distância e Tempo)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Limites de Embarque (Busca do Passageiro)", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Não gaste combustível e tempo para ir buscar passageiro muito longe.", color = TextMuted, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Distância máxima de busca:", color = TextMuted, fontSize = 13.sp)
                    Text("${String.format(Locale.ROOT, "%.1f", maxPickupDistanceKm)} km", color = TextWhite, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = maxPickupDistanceKm.toFloat(),
                    onValueChange = { maxPickupDistanceKm = Math.round(it * 10.0) / 10.0 },
                    valueRange = 1.0f..10.0f,
                    colors = SliderDefaults.colors(thumbColor = Emerald500, activeTrackColor = Emerald500)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tempo máximo até o embarque:", color = TextMuted, fontSize = 13.sp)
                    Text("$maxPickupTimeMinutes minutos", color = TextWhite, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = maxPickupTimeMinutes.toFloat(),
                    onValueChange = { maxPickupTimeMinutes = Math.round(it) },
                    valueRange = 3f..20f,
                    colors = SliderDefaults.colors(thumbColor = Emerald500, activeTrackColor = Emerald500)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Save Button
        Button(
            onClick = {
                viewModel.updateSettings(
                    settings.copy(
                        minRatePerKmGreen = minRatePerKmGreen,
                        minRatePerKmYellow = minRatePerKmYellow,
                        minRatePerHourGreen = minRatePerHourGreen,
                        minRatePerHourYellow = minRatePerHourYellow,
                        minPassengerRating = minPassengerRating,
                        minRideValue = minRideValue,
                        minProfitValue = minProfitValue,
                        minProfitPercent = minProfitPercent,
                        maxPickupDistanceKm = maxPickupDistanceKm,
                        maxPickupTimeMinutes = maxPickupTimeMinutes
                    )
                )
                Toast.makeText(context, "Regras e faixas do Semáforo atualizadas!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Emerald500),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("button_save_limits")
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Salvar Minhas Regras", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
