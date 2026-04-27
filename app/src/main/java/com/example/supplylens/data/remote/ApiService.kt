package com.supplylens.app.data.remote

import com.supplylens.app.data.model.*
import retrofit2.http.*

interface ApiService {

    // ── Real API Endpoints (Updated with actual contract) ──

    @GET("api/kpis")
    suspend fun getKpis(): KpiData

    @GET("api/alerts")
    suspend fun getAlerts(): List<AlertItem>

    @GET("api/shipments")
    suspend fun getShipments(): List<Shipment>

    @GET("api/scenarios")
    suspend fun getScenarios(): List<Scenario>

    @POST("api/simulate-disruption")
    suspend fun simulateDisruption(
        @Body request: SimulateDisruptionRequest
    ): SimulateDisruptionResponse

    @GET("api/cascade-map/{routeId}")
    suspend fun getCascadeMap(
        @Path("routeId") routeId: String
    ): CascadeMapResponse

    @POST("api/predict-disruption")
    suspend fun predictDisruption(
        @Body request: PredictDisruptionRequest
    ): PredictDisruptionResponse

    @POST("api/explain")
    suspend fun explainRisk(
        @Body request: ExplainRiskRequest
    ): ExplainRiskResponse

    @GET("api/network")
    suspend fun getNetwork(): NetworkData
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