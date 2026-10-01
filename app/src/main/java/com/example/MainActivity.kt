package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.screens.LimitsSettingsScreen
import com.example.ui.screens.RideHistoryScreen
import com.example.ui.screens.SecurityAlertsScreen
import com.example.ui.screens.SemaforoScreen
import com.example.ui.screens.VehicleCostScreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.Emerald400
import com.example.ui.theme.Emerald500
import com.example.ui.theme.Emerald900
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

enum class AppScreen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    SEMAFORO("Semáforo", Icons.Filled.Speed, Icons.Outlined.Speed, "nav_semaforo"),
    CUSTO_VEICULO("Custo Real", Icons.Filled.DirectionsCar, Icons.Outlined.DirectionsCar, "nav_custo_veiculo"),
    LIMITES("Limites", Icons.Filled.Tune, Icons.Outlined.Tune, "nav_limites"),
    HISTORICO("Histórico", Icons.Filled.History, Icons.Outlined.History, "nav_historico"),
    SEGURANCA("Segurança", Icons.Filled.Security, Icons.Outlined.Security, "nav_seguranca")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel()
                var currentScreen by remember { mutableStateOf(AppScreen.SEMAFORO) }

                BackHandler(enabled = currentScreen != AppScreen.SEMAFORO) {
                    currentScreen = AppScreen.SEMAFORO
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DarkBg,
                    bottomBar = {
                        NavigationBar(
                            containerColor = DarkCard,
                            contentColor = TextWhite,
                            modifier = Modifier
                                .windowInsetsPadding(WindowInsets.navigationBars)
                                .testTag("bottom_nav_bar")
                        ) {
                            AppScreen.entries.forEach { screen ->
                                val selected = currentScreen == screen
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentScreen = screen },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                            contentDescription = screen.title,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = screen.title,
                                            fontSize = 10.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Emerald400,
                                        selectedTextColor = Emerald400,
                                        unselectedIconColor = TextMuted,
                                        unselectedTextColor = TextMuted,
                                        indicatorColor = Emerald900
                                    ),
                                    modifier = Modifier.testTag(screen.tag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            AppScreen.SEMAFORO -> SemaforoScreen(
                                viewModel = viewModel,
                                onNavigateToVehicles = { currentScreen = AppScreen.CUSTO_VEICULO }
                            )
                            AppScreen.CUSTO_VEICULO -> VehicleCostScreen(viewModel = viewModel)
                            AppScreen.LIMITES -> LimitsSettingsScreen(viewModel = viewModel)
                            AppScreen.HISTORICO -> RideHistoryScreen(viewModel = viewModel)
                            AppScreen.SEGURANCA -> SecurityAlertsScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
