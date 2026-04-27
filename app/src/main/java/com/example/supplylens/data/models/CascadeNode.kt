package com.supplylens.app.data.model

data class CascadeNode(
    val id: String,
    val name: String,
    val type: String,       // "supplier" | "factory" | "warehouse" | "customer"
    val lat: Float,
    val lng: Float,
    var isAffected: Boolean = false
)

data class CascadeLink(
    val sourceId: String,
    val targetId: String,
    var isAffected: Boolean = false
)

data class NetworkData(
    val nodes: List<CascadeNode>,
    val links: List<CascadeLink>
)

data class Scenario(
    val id: String,
    val title: String,
    val description: String,
    val affectedNodeIds: List<String>
)


data class DashboardData(
    val kpis: KpiData,
    val alerts: List<AlertItem>,
    val shipments: List<Shipment>,
    val scenarios: List<Scenario>
)