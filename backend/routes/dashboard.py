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


@router.get("/api/alerts")
def get_alerts():
    return [
        {"id": "A001", "route": "Chongqing → Los Angeles",
         "severity": "CRITICAL", "message": "Typhoon approaching Taiwan Strait",
         "time_ago": "2 min ago"},
        {"id": "A002", "route": "Shenzhen → Tijuana",
         "severity": "HIGH", "message": "Port of LA labor strike escalation",
         "time_ago": "5 min ago"},
        {"id": "A003", "route": "Ho Chi Minh → Chongqing",
         "severity": "HIGH", "message": "Mexican trucking union 48h stoppage",
         "time_ago": "8 min ago"},
        {"id": "A004", "route": "Rotterdam → New York",
         "severity": "MEDIUM", "message": "EU customs delays at Rotterdam port",
         "time_ago": "12 min ago"}
    ]


@router.get("/api/shipments")
def get_shipments():
    return {
        "total": 15,
        "shipments": [
            {"id": "SHP-48293", "origin": "Chongqing Plant",
             "destination": "Los Angeles", "mode": "SEA",
             "status": "DELAYED", "eta_days": 18, "risk_score": 84, "value": 562000},
            {"id": "SHP-48292", "origin": "Shenzhen Components",
             "destination": "Tijuana Assembly", "mode": "SEA",
             "status": "AT_RISK", "eta_days": 14, "risk_score": 72, "value": 412000},
            {"id": "SHP-48300", "origin": "Ho Chi Minh Parts",
             "destination": "Chongqing Plant", "mode": "ROAD",
             "status": "DELAYED", "eta_days": 6, "risk_score": 66, "value": 287000},
            {"id": "SHP-48299", "origin": "Singapore Hub",
             "destination": "Newark DC", "mode": "SEA",
             "status": "AT_RISK", "eta_days": 4, "risk_score": 55, "value": 198000},
            {"id": "SHP-48301", "origin": "Mumbai Textiles",
             "destination": "London Distribution", "mode": "SEA",
             "status": "ON_TIME", "eta_days": 3, "risk_score": 30, "value": 145000}
        ]
    }


@router.get("/api/scenarios")
def get_scenarios():
    return [
        {"id": "SC1", "name": "Close Port of LA (72h)",
         "type": "PORT", "severity": "CRITICAL",
         "affected_nodes": 3, "cost_exposure": 1580000},
        {"id": "SC2", "name": "Taiwan Strait Typhoon",
         "type": "WEATHER", "severity": "CRITICAL",
         "affected_nodes": 4, "cost_exposure": 1580000},
        {"id": "SC3", "name": "Chongqing Plant Outage",
         "type": "FACILITY", "severity": "HIGH",
         "affected_nodes": 3, "cost_exposure": 890000},
        {"id": "SC5", "name": "Rotterdam Customs Freeze",
         "type": "CUSTOMS", "severity": "MEDIUM",
         "affected_nodes": 2, "cost_exposure": 320000},
        {"id": "SC6", "name": "Mexico Trucking Strike",
         "type": "STRIKE", "severity": "HIGH",
         "affected_nodes": 2, "cost_exposure": 445000}
    ]
