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
