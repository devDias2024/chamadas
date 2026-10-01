package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.EvaluationResult
import com.example.ui.theme.App99Yellow
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardElevated
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald900
import com.example.ui.theme.InDriveTeal
import com.example.ui.theme.SemaforoGreen
import com.example.ui.theme.SemaforoGreenBg
import com.example.ui.theme.SemaforoRed
import com.example.ui.theme.SemaforoRedBg
import com.example.ui.theme.SemaforoYellow
import com.example.ui.theme.SemaforoYellowBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.UberBlack
import java.util.Locale

@Composable
fun SemaforoCard(
    appName: String,
    grossAmount: Double,
    pickupDistanceKm: Double,
    pickupDurationMin: Int,
    rideDistanceKm: Double,
    rideDurationMin: Int,
    passengerName: String,
    passengerRating: Double,
    originAddress: String,
    destinationAddress: String,
    isNewPassenger: Boolean,
    hasStops: Boolean,
    evaluation: EvaluationResult,
    chatTemplate: String,
    isSecondaryLayout: Boolean = false,
    onSpeak: (() -> Unit)? = null,
    onAccept: (() -> Unit)? = null,
    onDecline: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val statusColor by animateColorAsState(
        targetValue = when (evaluation.status) {
            "ACEITAR" -> SemaforoGreen
            "ATENÇÃO" -> SemaforoYellow
            else -> SemaforoRed
        },
        animationSpec = tween(300),
        label = "statusColor"
    )

    val statusBgColor by animateColorAsState(
        targetValue = when (evaluation.status) {
            "ACEITAR" -> SemaforoGreenBg
            "ATENÇÃO" -> SemaforoYellowBg
            else -> SemaforoRedBg
        },
        animationSpec = tween(300),
        label = "statusBgColor"
    )

    val statusText = when (evaluation.status) {
        "ACEITAR" -> "ACEITAR"
        "ATENÇÃO" -> "ATENÇÃO"
        else -> "RECUSAR"
    }

    val statusIcon = when (evaluation.status) {
        "ACEITAR" -> Icons.Default.Check
        "ATENÇÃO" -> Icons.Default.Warning
        else -> Icons.Default.Close
    }

    if (isSecondaryLayout) {
        // Layout secundário de peças soltas (como no screenshot 2)
        SecondarySplitLayout(
            statusColor = statusColor,
            statusText = statusText,
            evaluation = evaluation,
            appName = appName,
            grossAmount = grossAmount,
            passengerRating = passengerRating,
            modifier = modifier
        )
        return
    }

    // Layout Completo (como no Screenshot 3 "Por cima do app de corrida")
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(22.dp))
            .testTag("semaforo_floating_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2A)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.verticalGradient(
                listOf(statusColor.copy(alpha = 0.8f), DarkBorder)
            ),
            width = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Action Badge Pill (Screenshot 3: "✓ ACEITAR Betim")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = statusColor,
                    modifier = Modifier.testTag("semaforo_verdict_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = statusText,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = statusText,
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // App Badge (Uber / 99 / inDrive)
                AppBadge(appName = appName)

                // TTS Voice announce button
                IconButton(
                    onClick = { onSpeak?.invoke() },
                    modifier = Modifier
                        .size(38.dp)
                        .background(DarkCardElevated, CircleShape)
                        .testTag("button_voice_readout")
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Ouvir avaliação em voz alta",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle Line (Screenshot 3: "Uber · 25 min · 12,9 km")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$appName · ${evaluation.totalDurationMin} min · ${String.format(Locale.ROOT, "%.1f", evaluation.totalDistanceKm)} km",
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Text(
                    text = "R$ ${String.format(Locale.ROOT, "%.2f", grossAmount)}",
                    color = Emerald400,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Metrics Grid with colored indicator bars (Screenshot 3: |2,17  |67  |5,00  |49)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0C131D),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(DarkBorder, DarkBorder)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetricColumn(
                        label = "R$/Km",
                        value = String.format(Locale.ROOT, "%.2f", evaluation.ratePerKm),
                        barColor = if (evaluation.ratePerKm >= 2.0) SemaforoGreen else if (evaluation.ratePerKm >= 1.5) SemaforoYellow else SemaforoRed,
                        testTag = "metric_rate_km"
                    )
                    MetricColumn(
                        label = "R$/Hora",
                        value = String.format(Locale.ROOT, "%.0f", evaluation.ratePerHour),
                        barColor = if (evaluation.ratePerHour >= 45.0) SemaforoGreen else if (evaluation.ratePerHour >= 30.0) SemaforoYellow else SemaforoRed,
                        testTag = "metric_rate_hour"
                    )
                    MetricColumn(
                        label = "Nota",
                        value = String.format(Locale.ROOT, "%.2f", passengerRating),
                        barColor = if (passengerRating >= 4.80) SemaforoGreen else if (passengerRating >= 4.65) SemaforoYellow else SemaforoRed,
                        testTag = "metric_rating"
                    )
                    MetricColumn(
                        label = "Lucro %",
                        value = "${String.format(Locale.ROOT, "%.0f", evaluation.profitMarginPercent)}%",
                        barColor = if (evaluation.profitMarginPercent >= 40.0) SemaforoGreen else if (evaluation.profitMarginPercent >= 25.0) SemaforoYellow else SemaforoRed,
                        testTag = "metric_profit_margin"
                    )
                }
            }

            // Route details (Village do Lago -> Ibituruna)
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCardElevated.copy(alpha = 0.5f))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Emerald500, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = originAddress,
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${pickupDurationMin} min",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .border(2.dp, statusColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = destinationAddress,
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${rideDurationMin} min",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // Warning Badges / Alert Seals (Screenshot 6 & 3)
            val hasBadges = evaluation.hasRiskAlert || evaluation.hasSupermarketAlert || isNewPassenger || hasStops
            if (hasBadges) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (evaluation.hasRiskAlert) {
                        AlertBadge(
                            text = "Área de risco",
                            icon = Icons.Default.Warning,
                            bgColor = SemaforoRedBg,
                            contentColor = SemaforoRed
                        )
                    }
                    if (evaluation.hasSupermarketAlert) {
                        AlertBadge(
                            text = "Supermercado",
                            icon = Icons.Default.ShoppingBag,
                            bgColor = SemaforoYellowBg,
                            contentColor = SemaforoYellow
                        )
                    }
                    if (isNewPassenger) {
                        AlertBadge(
                            text = "Usuário iniciante",
                            icon = Icons.Default.Star,
                            bgColor = Color(0xFF0F3942),
                            contentColor = Color(0xFF38BDF8)
                        )
                    }
                    if (hasStops) {
                        AlertBadge(
                            text = "Com parada",
                            icon = Icons.AutoMirrored.Filled.DirectionsRun,
                            bgColor = Color(0xFF351F49),
                            contentColor = Color(0xFFC084FC)
                        )
                    }
                }
            }

            // Quick Tools Row: Copiar Chat & Navegação no Maps
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button Copiar Chat personalizado (Screenshot 3)
                Button(
                    onClick = {
                        val message = chatTemplate
                            .replace("{nome}", passengerName)
                            .replace("{tempo}", pickupDurationMin.toString())
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Rota Pro Chat", message)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Mensagem copiada para o chat!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("button_copy_chat"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCardElevated),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar Chat",
                        tint = Emerald400,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Copiar Chat",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Button Navegar (Google Maps / Waze)
                Button(
                    onClick = {
                        val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(originAddress))
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            val webMapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=" + Uri.encode(originAddress)))
                            context.startActivity(webMapIntent)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("button_navigate_maps"),
                    colors = ButtonDefaults.buttonColors(containerColor = DarkCardElevated),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Navegar Maps",
                        tint = Color(0xFF60A5FA),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Navegar",
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Accept / Decline action row if provided
            if (onAccept != null || onDecline != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onDecline != null) {
                        Button(
                            onClick = onDecline,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("button_decline_ride"),
                            colors = ButtonDefaults.buttonColors(containerColor = SemaforoRedBg),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(SemaforoRed, SemaforoRed))),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Recusar", color = SemaforoRed, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (onAccept != null) {
                        Button(
                            onClick = onAccept,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("button_accept_ride"),
                            colors = ButtonDefaults.buttonColors(containerColor = SemaforoGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Aceitar", color = Color.Black, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricColumn(
    label: String,
    value: String,
    barColor: Color,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.testTag(testTag)
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            // Colored vertical bar as seen in screenshot 3 ("|2,17")
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(barColor)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun AppBadge(appName: String) {
    val (bg, textCol) = when (appName.lowercase(Locale.ROOT)) {
        "99" -> App99Yellow to Color.Black
        "indrive" -> InDriveTeal to Color.White
        else -> UberBlack to Color.White
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        modifier = Modifier.border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
    ) {
        Text(
            text = appName,
            color = textCol,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun AlertBadge(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    bgColor: Color,
    contentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        modifier = Modifier.border(1.dp, contentColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = text,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun SecondarySplitLayout(
    statusColor: Color,
    statusText: String,
    evaluation: EvaluationResult,
    appName: String,
    grossAmount: Double,
    passengerRating: Double,
    modifier: Modifier = Modifier
) {
    // Secondary split floating bubbles layout
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bubble 1: Verdict & App
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = statusColor,
            modifier = Modifier.shadow(8.dp, RoundedCornerShape(14.dp))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = appName, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = statusText, color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.Black)
            }
        }

        // Bubble 2: Key Numbers
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF131D2A),
            border = CardDefaults.outlinedCardBorder().copy(width = 1.dp, brush = Brush.linearGradient(listOf(statusColor, DarkBorder))),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("R$/km", color = TextMuted, fontSize = 10.sp)
                    Text(
                        String.format(Locale.ROOT, "%.2f", evaluation.ratePerKm),
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("R$/h", color = TextMuted, fontSize = 10.sp)
                    Text(
                        String.format(Locale.ROOT, "%.0f", evaluation.ratePerHour),
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Lucro", color = TextMuted, fontSize = 10.sp)
                    Text(
                        "R$ ${String.format(Locale.ROOT, "%.1f", evaluation.netProfit)}",
                        color = Emerald400,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
