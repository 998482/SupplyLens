package com.supplylens.app.ui.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.supplylens.app.ui.components.*
import com.supplylens.app.ui.theme.*
import com.supplylens.app.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    val displayKpis = state.simulatedKpis ?: state.kpis
    val isSimulated = state.simulatedKpis != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = CyanAccent
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                item {
                    Column {
                        Text(
                            "SupplyLens",
                            color = CyanAccent,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "Supply Chain Intelligence Dashboard",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }

                // KPI Cards
                item {
                    displayKpis?.let { kpis ->
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                KpiCard(
                                    title = "ON-TIME DELIVERY",
                                    value = "${String.format("%.1f", kpis.onTimeRate)}%",
                                    subtitle = "of shipments on schedule",
                                    accentColor = GreenOk,
                                    isSimulated = isSimulated,
                                    modifier = Modifier.weight(1f)
                                )
                                KpiCard(
                                    title = "AT-RISK SHIPMENTS",
                                    value = "${kpis.atRiskCount}",
                                    subtitle = "need immediate action",
                                    accentColor = RedDanger,
                                    isSimulated = isSimulated,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                KpiCard(
                                    title = "COST EXPOSURE",
                                    value = "\$${String.format("%.2f", kpis.costExposure / 1_000_000)}M",
                                    subtitle = "potential financial loss",
                                    accentColor = OrangeAccent,
                                    isSimulated = isSimulated,
                                    modifier = Modifier.weight(1f)
                                )
                                KpiCard(
                                    title = "AVG DELAY",
                                    value = "${String.format("%.1f", kpis.avgDelayDays)}d",
                                    subtitle = "average disruption time",
                                    accentColor = YellowWarn,
                                    isSimulated = isSimulated,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // What-If Simulator
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "⚡ What-If Simulator",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Toggle scenarios to see cascade impact",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            state.scenarios.forEach { scenario ->
                                ScenarioToggle(
                                    title = scenario.title,
                                    isActive = scenario.id in state.activeScenarioIds,
                                    onClick = { viewModel.toggleScenario(scenario.id) }
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }

                // Alerts
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "🔴 Live Disruption Feed",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(12.dp))
                            state.alerts.forEach { alert ->
                                AlertCard(
                                    severity = alert.severity,
                                    title = alert.title,
                                    location = alert.location,
                                    affectedNodes = alert.affectedNodes
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }

                // Shipments table
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column {
                            Text(
                                "📦 Shipment Tracker",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(16.dp)
                            )
                            state.shipments.forEach { s ->
                                ShipmentRow(
                                    id = s.id,
                                    origin = s.origin,
                                    destination = s.destination,
                                    status = if (s.id in state.affectedShipmentIds) "at-risk" else s.status,
                                    etaDays = s.etaDays,
                                    riskScore = s.riskScore,
                                    value = s.valueDollars,
                                    isAffected = s.id in state.affectedShipmentIds
                                )
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}