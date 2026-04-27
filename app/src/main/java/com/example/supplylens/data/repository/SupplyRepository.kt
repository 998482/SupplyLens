package com.supplylens.app.data.repository

import android.util.Log
import com.supplylens.app.data.model.*
import com.supplylens.app.data.remote.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupplyRepository @Inject constructor(
    private val api: ApiService
) {

    companion object {
        private const val TAG = "SupplyRepository"
    }

    // ── Fallback Mock Data (for when API fails) ───────────────────────────

    fun getMockShipments() = listOf(
        Shipment("SHP-001", "Shanghai",   "Chicago",     "sea",  "in-transit", 14, 0.2,  320000.0),
        Shipment("SHP-002", "Taipei",     "Los Angeles", "air",  "at-risk",     3, 0.85, 180000.0),
        Shipment("SHP-003", "Mumbai",     "Rotterdam",   "sea",  "delayed",    21, 0.9,   95000.0),
        Shipment("SHP-004", "Shenzhen",   "Tokyo",       "sea",  "in-transit",  7, 0.3,  450000.0),
        Shipment("SHP-005", "Chongqing",  "London",      "rail", "at-risk",    18, 0.75, 220000.0),
        Shipment("SHP-006", "Bratislava", "Newark",      "road", "delivered",   0, 0.05, 130000.0),
        Shipment("SHP-007", "Singapore",  "Sydney",      "sea",  "in-transit",  5, 0.15, 280000.0),
        Shipment("SHP-008", "LA Port",    "Chicago",     "road", "at-risk",     4, 0.8,  175000.0),
        Shipment("SHP-009", "Rotterdam",  "London",      "road", "in-transit",  2, 0.1,   90000.0),
        Shipment("SHP-010", "Shanghai",   "Sydney",      "sea",  "delayed",    28, 0.88, 310000.0)
    )

    fun getMockKpis() = KpiData(
        onTimeRate   = 66.7f,
        atRiskCount  = 5,
        costExposure = 1_580_000.0,
        avgDelayDays = 13.8f
    )

    fun getMockAlerts() = listOf(
        AlertItem("A1", "critical", "Typhoon approaching Taiwan Strait", "Taiwan",      4),
        AlertItem("A2", "critical", "LA Port labor strike",              "Los Angeles", 6),
        AlertItem("A3", "critical", "Mexico trucking stoppage",          "Tijuana",     3),
        AlertItem("A4", "warning",  "Rotterdam customs delay",           "Rotterdam",   2),
        AlertItem("A5", "warning",  "Winter storm disruption",           "Chicago",     2),
        AlertItem("A6", "info",     "Singapore capacity update",         "Singapore",   0)
    )

    fun getMockScenarios() = listOf(
        Scenario("SC1", "Close Port of LA (72h)",   "Dock workers strike shuts LA port",
            listOf("la_port", "chicago_customer")),
        Scenario("SC2", "Taiwan Strait Typhoon",    "Category 4 typhoon disrupts Taiwan shipping",
            listOf("taipei_supplier", "shenzhen_supplier")),
        Scenario("SC3", "Chongqing Plant Outage",   "Factory fire halts production",
            listOf("chongqing_factory")),
        Scenario("SC4", "Red Sea Rerouting",        "Houthi attacks force Cape of Good Hope reroute",
            listOf("rotterdam_port", "singapore_port")),
        Scenario("SC5", "Rotterdam Customs Freeze", "EU customs audit halts clearance",
            listOf("rotterdam_port")),
        Scenario("SC6", "Mexico Trucking Strike",   "Cartels block major trucking routes",
            listOf("tijuana_factory"))
    )

    fun getMockNodes() = listOf(
        CascadeNode("shanghai_supplier",  "Shanghai",   "supplier",  31.2f,  121.5f),
        CascadeNode("shenzhen_supplier",  "Shenzhen",   "supplier",  22.5f,  114.1f),
        CascadeNode("taipei_supplier",    "Taipei",     "supplier",  25.0f,  121.5f),
        CascadeNode("mumbai_supplier",    "Mumbai",     "supplier",  19.1f,   72.9f),
        CascadeNode("chongqing_factory",  "Chongqing",  "factory",   29.6f,  106.6f),
        CascadeNode("tijuana_factory",    "Tijuana",    "factory",   32.5f, -117.0f),
        CascadeNode("bratislava_factory", "Bratislava", "factory",   48.1f,   17.1f),
        CascadeNode("la_port",            "LA Port",    "warehouse", 33.7f, -118.2f),
        CascadeNode("rotterdam_port",     "Rotterdam",  "warehouse", 51.9f,    4.5f),
        CascadeNode("singapore_port",     "Singapore",  "warehouse",  1.3f,  103.8f),
        CascadeNode("newark_port",        "Newark",     "warehouse", 40.7f,  -74.2f),
        CascadeNode("chicago_customer",   "Chicago",    "customer",  41.9f,  -87.6f),
        CascadeNode("london_customer",    "London",     "customer",  51.5f,   -0.1f),
        CascadeNode("tokyo_customer",     "Tokyo",      "customer",  35.7f,  139.7f),
        CascadeNode("sydney_customer",    "Sydney",     "customer", -33.9f,  151.2f)
    )

    private val nodeIdToCityName = mapOf(
        "shanghai_supplier"  to "Shanghai",
        "shenzhen_supplier"  to "Shenzhen",
        "taipei_supplier"    to "Taipei",
        "mumbai_supplier"    to "Mumbai",
        "chongqing_factory"  to "Chongqing",
        "tijuana_factory"    to "Tijuana",
        "bratislava_factory" to "Bratislava",
        "la_port"            to "LA Port",
        "rotterdam_port"     to "Rotterdam",
        "singapore_port"     to "Singapore",
        "newark_port"        to "Newark",
        "chicago_customer"   to "Chicago",
        "london_customer"    to "London",
        "tokyo_customer"     to "Tokyo",
        "sydney_customer"    to "Sydney"
    )

    // ── Real API Calls ─────────────────────────────────────────────────────

    suspend fun loadDashboard() = coroutineScope {
        try {
            Log.d(TAG, "Loading dashboard from real API...")
            val kpis      = async {
                try {
                    api.getKpis()
                } catch (e: Exception) {
                    Log.e(TAG, "KPI fetch failed: ${e.message}")
                    getMockKpis()
                }
            }
            val alerts    = async {
                try {
                    api.getAlerts()
                } catch (e: Exception) {
                    Log.e(TAG, "Alerts fetch failed: ${e.message}")
                    getMockAlerts()
                }
            }
            val shipments = async {
                try {
                    api.getShipments()
                } catch (e: Exception) {
                    Log.e(TAG, "Shipments fetch failed: ${e.message}")
                    getMockShipments()
                }
            }
            val scenarios = async {
                try {
                    api.getScenarios()
                } catch (e: Exception) {
                    Log.e(TAG, "Scenarios fetch failed: ${e.message}")
                    getMockScenarios()
                }
            }
            DashboardData(
                kpis.await(),
                alerts.await(),
                shipments.await(),
                scenarios.await()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Dashboard load failed completely: ${e.message}")
            // Return mock data as last resort
            DashboardData(getMockKpis(), getMockAlerts(), getMockShipments(), getMockScenarios())
        }
    }

    suspend fun simulate(scenarioIds: List<String>): SimulateResponse {
        return try {
            Log.d(TAG, "Simulating scenarios: $scenarioIds")
            // Use the first scenario for the API call
            val firstScenario = getMockScenarios().find { it.id == scenarioIds.firstOrNull() }
            if (firstScenario != null) {
                val response = api.simulateDisruption(
                    SimulateDisruptionRequest(
                        route_id = firstScenario.id,
                        scenario = firstScenario.title
                    )
                )
                // Convert API response to our model
                SimulateResponse(
                    affectedNodes = response.affected_routes,
                    impactedShipmentIds = response.affected_routes,
                    projectedKpis = KpiData(
                        onTimeRate = (getMockKpis().onTimeRate - response.delay_days * 2).coerceAtLeast(10f),
                        atRiskCount = (response.affected_routes.size * 2).coerceAtLeast(1),
                        costExposure = response.total_cost,
                        avgDelayDays = response.delay_days.toFloat()
                    )
                )
            } else {
                throw Exception("No scenario found")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Simulation API failed: ${e.message}, using mock fallback")
            // Fallback to mock simulation
            val affectedNodeIds = getMockScenarios()
                .filter { it.id in scenarioIds }
                .flatMap { it.affectedNodeIds }
                .toSet()

            val affectedCityNames = affectedNodeIds
                .mapNotNull { nodeIdToCityName[it] }
                .map { it.lowercase() }
                .toSet()

            val impacted = getMockShipments()
                .filter { s ->
                    affectedCityNames.any { city ->
                        s.origin.lowercase().contains(city) ||
                                s.destination.lowercase().contains(city)
                    }
                }
                .map { it.id }

            val baseKpi = getMockKpis()
            SimulateResponse(
                affectedNodes       = affectedNodeIds.toList(),
                impactedShipmentIds = impacted,
                projectedKpis       = baseKpi.copy(
                    onTimeRate   = (baseKpi.onTimeRate - scenarioIds.size * 7f).coerceAtLeast(10f),
                    atRiskCount  = baseKpi.atRiskCount + scenarioIds.size * 2,
                    costExposure = baseKpi.costExposure + scenarioIds.size * 400_000.0
                )
            )
        }
    }

    suspend fun predictDisruption(
        origin: String,
        destination: String,
        distanceKm: Double,
        weather: String,
        trafficLevel: Int
    ): PredictDisruptionResponse {
        return try {
            Log.d(TAG, "Predicting disruption for $origin -> $destination")
            api.predictDisruption(
                PredictDisruptionRequest(
                    origin = origin,
                    destination = destination,
                    distance_km = distanceKm,
                    weather = weather,
                    traffic_level = trafficLevel
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Predict disruption failed: ${e.message}")
            // Return mock prediction
            PredictDisruptionResponse(
                risk_percent = 35.0,
                risk_level = "MEDIUM",
                confidence = 0.85
            )
        }
    }

    suspend fun getCascadeMap(routeId: String): CascadeMapResponse {
        return try {
            Log.d(TAG, "Getting cascade map for route: $routeId")
            api.getCascadeMap(routeId)
        } catch (e: Exception) {
            Log.e(TAG, "Cascade map fetch failed: ${e.message}")
            // Return mock cascade map
            CascadeMapResponse(
                nodes = getMockNodes().map {
                    CascadeNodeResponse(
                        id = it.id,
                        name = it.name,
                        risk_score = 0.5,
                        orders_affected = 3,
                        delay_days = 2
                    )
                },
                edges = listOf(),
                disrupted_node = ""
            )
        }
    }

    suspend fun explainRisk(
        routeId: String,
        riskData: Map<String, Any>
    ): ExplainRiskResponse {
        return try {
            Log.d(TAG, "Explaining risk for route: $routeId")
            api.explainRisk(
                ExplainRiskRequest(
                    route_id = routeId,
                    risk_data = riskData
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Explain risk failed: ${e.message}")
            // Return mock explanation
            ExplainRiskResponse(
                why_at_risk = "This route has historical delays due to weather patterns",
                cascade_impact = "Delays here will affect downstream warehouse operations",
                recommended_actions = "Consider alternate routing or increase buffer time"
            )
        }
    }

    suspend fun getGeminiExplanation(scenario: Scenario, kpis: KpiData): String {
        // First try API explanation
        return try {
            val response = explainRisk(
                routeId = scenario.id,
                riskData = mapOf("scenario" to scenario.title)
            )
            """
🔍 ${scenario.title} — Impact Analysis

📍 Affected Regions: ${scenario.affectedNodeIds.joinToString(", ")}

⚠️ Risk Level: HIGH

💰 Estimated Financial Exposure: ${"$"}${String.format("%.2fM", kpis.costExposure / 1_000_000)}

❓ Why at Risk:
${response.why_at_risk}

📦 Cascade Impact:
${response.cascade_impact}

✅ Recommended Actions:
${response.recommended_actions}

📊 Recovery Timeline: 72-96 hours if action taken now.
            """.trimIndent()
        } catch (e: Exception) {
            Log.e(TAG, "Gemini explanation failed: ${e.message}, using fallback")
            // Fallback explanation
            """
🔍 ${scenario.title} — Impact Analysis

📍 Affected Regions: ${scenario.affectedNodeIds.joinToString(", ")}

⚠️ Risk Level: HIGH

💰 Estimated Financial Exposure: ${"$"}${String.format("%.2fM", kpis.costExposure / 1_000_000)}

📦 Cascade Effect:
The disruption at ${scenario.affectedNodeIds.firstOrNull() ?: "affected region"} will propagate to downstream nodes within 24-48 hours, affecting shipments bound for end customers.

✅ Recommended Actions:
1. Activate alternate supplier contracts immediately
2. Reroute via ${
                if (scenario.affectedNodeIds.any { "taipei" in it || "shenzhen" in it })
                    "South China Sea alternate lanes"
                else
                    "backup hubs"
            }
3. Notify affected customers of potential 5-14 day delay
4. Increase safety stock at unaffected warehouses

📊 Recovery Timeline: 72-96 hours if action taken now.
            """.trimIndent()
        }
    }
}