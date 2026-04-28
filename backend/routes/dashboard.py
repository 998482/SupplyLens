from fastapi import APIRouter
from schemas import DashboardResponse, Alert

router = APIRouter()

@router.get("/api/dashboard-stats", response_model=DashboardResponse)
def get_dashboard():
    return DashboardResponse(
        routes_monitored=15,
        high_risk=5,
        orders_at_risk=847,
        avg_delay_days=13.8,
        alerts=[
            Alert(route="Chongqing → Los Angeles", risk="HIGH", orders_affected=234, delay_days=18.0),
            Alert(route="Shenzhen → Tijuana", risk="HIGH", orders_affected=189, delay_days=14.0),
            Alert(route="Ho Chi Minh → Chongqing", risk="HIGH", orders_affected=156, delay_days=6.0),
            Alert(route="Singapore → Newark", risk="MEDIUM", orders_affected=98, delay_days=4.5),
            Alert(route="Mumbai → London", risk="MEDIUM", orders_affected=67, delay_days=3.2),
            Alert(route="Tokyo → Los Angeles", risk="LOW", orders_affected=43, delay_days=1.1),
            Alert(route="Rotterdam → New York", risk="LOW", orders_affected=60, delay_days=1.5),
        ]
    )

@router.get("/api/kpis")
def get_kpis():
    return {
        "total_shipments": 156,
        "active_routes": 15,
        "disrupted_routes": 5,
        "avg_delay_days": 13.8,
        "total_value_at_risk": 450000.0,
        "on_time_delivery_rate": 0.87
    }

@router.get("/api/alerts")
def get_alerts():
    return [
        {
            "id": "ALT-001",
            "severity": "critical",
            "title": "Port Congestion - Chongqing",
            "location": "Chongqing → Los Angeles",
            "affectedNodes": 234
        },
        {
            "id": "ALT-002",
            "severity": "critical",
            "title": "Weather Disruption - Shenzhen",
            "location": "Shenzhen → Tijuana",
            "affectedNodes": 189
        },
        {
            "id": "ALT-003",
            "severity": "warning",
            "title": "Customs Delay - Mumbai",
            "location": "Mumbai → London",
            "affectedNodes": 67
        },
        {
            "id": "ALT-004",
            "severity": "warning",
            "title": "Delay - Singapore",
            "location": "Singapore → Newark",
            "affectedNodes": 98
        },
        {
            "id": "ALT-005",
            "severity": "info",
            "title": "Minor Delay - Tokyo",
            "location": "Tokyo → Los Angeles",
            "affectedNodes": 43
        }
    ]

@router.get("/api/shipments")
def get_shipments():
    return [
        {"id": "SHP-001", "origin": "Shanghai", "destination": "Chicago", "mode": "sea", "status": "in-transit", "delay_days": 14, "risk_score": 0.2, "value": 320000.0},
        {"id": "SHP-002", "origin": "Taipei", "destination": "Los Angeles", "mode": "air", "status": "at-risk", "delay_days": 3, "risk_score": 0.85, "value": 180000.0},
        {"id": "SHP-003", "origin": "Mumbai", "destination": "Rotterdam", "mode": "sea", "status": "delayed", "delay_days": 21, "risk_score": 0.9, "value": 95000.0}
    ]

@router.get("/api/scenarios")
def get_scenarios():
    return [
        {
            "id": "SCN-001",
            "title": "Port Strike",
            "description": "Major port workers strike",
            "affected_routes": 8,
            "estimated_delay": 7,
            "affectedNodeIds": ["N1", "N2", "N3"]
        },
        {
            "id": "SCN-002",
            "title": "Weather Disruption",
            "description": "Severe storm in Pacific",
            "affected_routes": 5,
            "estimated_delay": 4,
            "affectedNodeIds": ["N1", "N4"]
        },
        {
            "id": "SCN-003",
            "title": "Customs Delay",
            "description": "New customs regulations",
            "affected_routes": 3,
            "estimated_delay": 2,
            "affectedNodeIds": ["N2"]
        }
    ]

@router.get("/api/network")
def get_network():
    return {
        "nodes": [
            {"id": "N1", "name": "Shanghai", "type": "origin"},
            {"id": "N2", "name": "Chicago", "type": "destination"},
            {"id": "N3", "name": "Mumbai", "type": "origin"},
            {"id": "N4", "name": "Rotterdam", "type": "hub"}
        ],
        "edges": [
            {"from": "N1", "to": "N2", "risk": 0.3},
            {"from": "N3", "to": "N4", "risk": 0.6},
            {"from": "N4", "to": "N2", "risk": 0.2}
        ]
    }