from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import List
from services.gemini_service import explain_disruption
from services.simulate import simulate_disruption

router = APIRouter()

class ExplainRequest(BaseModel):
    route_id: str
    risk_data: dict

class ExplainResponse(BaseModel):
    why_at_risk: str
    cascade_impact: str
    recommended_actions: str

class SimulateRequest(BaseModel):
    route_id: str
    scenario: str

class SimulateResponse(BaseModel):
    affected_routes: List[str]
    total_cost: float
    delay_days: int
    report: str

@router.post("/api/explain", response_model=ExplainResponse)
def explain_risk(request: ExplainRequest):
    try:
        result = explain_disruption(request.route_id, request.risk_data)
        return ExplainResponse(**result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@router.post("/api/simulate-disruption", response_model=SimulateResponse)
def simulate(request: SimulateRequest):
    try:
        result = simulate_disruption(request.route_id, request.scenario)
        return SimulateResponse(**result)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
