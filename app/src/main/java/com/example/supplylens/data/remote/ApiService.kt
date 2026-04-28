package com.supplylens.app.data.remote

import com.supplylens.app.data.model.*
import retrofit2.http.*
import com.example.supplylens.data.remote.ApiEndpoints

interface ApiService {

    // ── Real API Endpoints (Updated with actual contract) ──

    @GET(ApiEndpoints.KPI)
    suspend fun getKpis(): KpiData

    @GET(ApiEndpoints.ALERTS)
    suspend fun getAlerts(): List<AlertItem>

    @GET(ApiEndpoints.SHIPMENTS)
    suspend fun getShipments(): List<Shipment>

    @GET(ApiEndpoints.SCENARIOS)
    suspend fun getScenarios(): List<Scenario>

    @POST(ApiEndpoints.SIMULATE)
    suspend fun simulateDisruption(@Body request: SimulateDisruptionRequest): SimulateDisruptionResponse

    @GET(ApiEndpoints.CASCADE_MAP)
    suspend fun getCascadeMap(@Path("route_id") routeId: String): CascadeMapResponse

    @POST(ApiEndpoints.PREDICT)
    suspend fun predictDisruption(@Body request: PredictDisruptionRequest): PredictDisruptionResponse

    @POST(ApiEndpoints.EXPLAIN)
    suspend fun explainRisk(@Body request: ExplainRiskRequest): ExplainRiskResponse
}

// ── Request Models ──

data class PredictDisruptionRequest(
    val origin: String,
    val destination: String,
    val distance_km: Double,
    val weather: String,
    val traffic_level: Int
)

data class SimulateDisruptionRequest(
    val route_id: String,
    val scenario: String
)

data class ExplainRiskRequest(
    val route_id: String,
    val risk_data: Map<String, Any>
)

// ── Response Models ──

data class PredictDisruptionResponse(
    val risk_percent: Double,
    val risk_level: String,
    val confidence: Double
)

data class SimulateDisruptionResponse(
    val affected_routes: List<String>,
    val total_cost: Double,
    val delay_days: Int,
    val report: String
)

data class CascadeMapResponse(
    val nodes: List<CascadeNodeResponse>,
    val edges: List<CascadeEdgeResponse>,
    val disrupted_node: String
)

data class CascadeNodeResponse(
    val id: String,
    val name: String,
    val risk_score: Double,
    val orders_affected: Int,
    val delay_days: Int
)

data class CascadeEdgeResponse(
    val from: String,
    val to: String
)

data class ExplainRiskResponse(
    val why_at_risk: String,
    val cascade_impact: String,
    val recommended_actions: String
)