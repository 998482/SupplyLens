package com.supplylens.app.ui.screens.routecheck

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.supplylens.app.ui.components.ShipmentRow
import com.supplylens.app.ui.components.StatChip
import com.supplylens.app.ui.theme.*
import com.supplylens.app.viewmodel.DashboardViewModel

@Composable
fun RouteCheckScreen(viewModel: DashboardViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                "🔍 Route Check",
                color = CyanAccent,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "Live shipment risk overview",
                color = TextMuted,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(16.dp))

            // Summary bar
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val atRisk = state.shipments.count {
                    it.status == "at-risk" || it.id in state.affectedShipmentIds
                }
                val delayed = state.shipments.count { it.status == "delayed" }
                val onTime = state.shipments.count { it.status == "in-transit" }

                StatChip("At Risk", "$atRisk", RedDanger)
                StatChip("Delayed", "$delayed", YellowWarn)
                StatChip("In Transit", "$onTime", CyanAccent)
            }

            Spacer(Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    Text(
                        "📦 All Shipments",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                    if (state.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = CyanAccent)
                        }
                    } else {
                        state.shipments.forEach { s ->
                            ShipmentRow(
                                id          = s.id,
                                origin      = s.origin,
                                destination = s.destination,
                                status      = if (s.id in state.affectedShipmentIds) "at-risk" else s.status,
                                etaDays     = s.etaDays,
                                riskScore   = s.riskScore,
                                value       = s.valueDollars,
                                isAffected  = s.id in state.affectedShipmentIds
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}