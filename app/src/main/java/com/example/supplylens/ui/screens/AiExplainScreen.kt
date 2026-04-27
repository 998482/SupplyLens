package com.supplylens.app.ui.screens.aiexplain

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
import com.supplylens.app.ui.theme.*
import com.supplylens.app.viewmodel.AiExplainViewModel

import com.supplylens.app.viewmodel.DashboardViewModel

@Composable
fun AiExplainScreen(
    aiViewModel: AiExplainViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    val explanation by aiViewModel.explanation.collectAsState()
    val isLoading by aiViewModel.isLoading.collectAsState()
    val dashboardState by dashboardViewModel.uiState.collectAsState()

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
                "🤖 AI Insight",
                color = CyanAccent,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "Scenario impact analysis with recommendations",
                color = TextMuted,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(16.dp))

            // Scenarios list
            if (dashboardState.scenarios.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Select a Scenario",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(12.dp))
                        dashboardState.scenarios.forEach { scenario ->
                            ScenarioButton(
                                title = scenario.title,
                                isActive = scenario.id == aiViewModel.selectedScenarioId.value,
                                onClick = {
                                    dashboardState.kpis?.let { kpis ->
                                        aiViewModel.explain(scenario, kpis)
                                    }
                                }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }

            // Explanation display
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CyanAccent)
                }
            } else if (explanation != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        explanation ?: "",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(16.dp),
                        lineHeight = 18.sp
                    )
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, BorderColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Select a scenario to view AI analysis",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ScenarioButton(
    title: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isActive) OrangeAccent.copy(alpha = 0.2f) else SurfaceCard
    val borderColor = if (isActive) OrangeAccent else BorderColor

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bgColor,
            contentColor = TextPrimary
        ),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Text(
            title,
            fontSize = 13.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}