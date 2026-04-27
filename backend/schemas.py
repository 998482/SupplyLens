from pydantic import BaseModel, Field
from typing import List

class PredictRequest(BaseModel):
    origin: str
    destination: str
    distance_km: int
    weather: str
    traffic_level: int

class PredictResponse(BaseModel):
    risk_percent: float
    risk_level: str
    confidence: float

class Alert(BaseModel):
    route: str
    risk: str
    orders_affected: int
    delay_days: float

class DashboardResponse(BaseModel):
    routes_monitored: int
    high_risk: int
    orders_at_risk: int
    avg_delay_days: float
    alerts: List[Alert]

class CascadeNode(BaseModel):
    id: str
    name: str
    risk_score: int
    orders_affected: int
    delay_days: float

class CascadeEdge(BaseModel):
    from_node: str = Field(alias="from")
    to_node: str = Field(alias="to")
    model_config = {"populate_by_name": True}

class CascadeMapResponse(BaseModel):
    nodes: List[CascadeNode]
    edges: List[CascadeEdge]
    disrupted_node: str

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
