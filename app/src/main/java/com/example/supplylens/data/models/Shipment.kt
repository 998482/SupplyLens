package com.supplylens.app.data.model

data class Shipment(
    val id: String,
    val origin: String,
    val destination: String,
    val mode: String,          // "sea" | "air" | "road" | "rail"
    val status: String,        // "in-transit" | "at-risk" | "delayed" | "delivered"
    val etaDays: Int,
    val riskScore: Double,      // 0.0 - 1.0
    val valueDollars: Double
)

data class KpiData(
    val onTimeRate: Float,
    val atRiskCount: Int,
    val costExposure: Double,
    val avgDelayDays: Float
)

data class AlertItem(
    val id: String,
    val severity: String,      // "critical" | "warning" | "info"
    val title: String,
    val location: String,
    val affectedNodes: Int
)

data class SimulateRequest(val scenarioIds: List<String>)

data class SimulateResponse(
    val affectedNodes: List<String>,
    val impactedShipmentIds: List<String>,
    val projectedKpis: KpiData
)