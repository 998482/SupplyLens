from fastapi import APIRouter
from schemas import ExplainRequest, ExplainResponse, SimulateRequest, SimulateResponse

router = APIRouter()

@router.post("/api/explain", response_model=ExplainResponse)
def explain_risk(request: ExplainRequest):
    return ExplainResponse(
        why_at_risk="Typhoon warning near Taiwan Strait is affecting Chongqing shipments. Port of LA labor strike causing 96-hour work stoppage with 14 vessels queued. Combined disruption pushes risk to critical level.",
        cascade_impact="Primary disruption at Chongqing Plant cascades to Tijuana Assembly (14d delay), then Newark DC (8d delay), affecting 3 downstream nodes. Total orders at risk: 568 across North America.",
        recommended_actions="1. Reroute Chongqing shipments via Singapore Hub. 2. Activate backup supplier in Ho Chi Minh City. 3. Pre-position inventory at Newark DC. 4. Alert Tijuana Assembly to prepare for reduced inflow."
    )

@router.post("/api/simulate-disruption", response_model=SimulateResponse)
def simulate_disruption(request: SimulateRequest):
    return SimulateResponse(
        affected_routes=[
            "Chongqing → Los Angeles",
            "Shenzhen → Tijuana",
            "Tijuana → Newark DC",
            "Newark DC → London"
        ],
        total_cost=1580000.0,
        delay_days=18,
        report="SIMULATION REPORT - Taiwan Strait Typhoon Scenario\n\nCritical disruption at Chongqing Plant (node_1). Cascade shows 4 downstream routes affected, combined exposure $1.58M USD.\n\nTier 1: Tijuana Assembly - 14 day delay, 189 orders held.\nTier 2: Newark DC backup stock depleted in 72 hours.\nTier 3: London Distribution faces stockout by Day 21.\n\nRecommend: Activate Red Sea rerouting. Recovery: 23 days."
    )
