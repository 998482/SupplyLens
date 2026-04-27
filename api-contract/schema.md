# SupplyLens API Contract — LOCKED AFTER DAY 1

## 1. Predict Disruption
POST /api/predict-disruption
Request:
{
  "origin": "string",
  "destination": "string",
  "distance_km": number,
  "weather": "string",
  "traffic_level": number
}
Response:
{
  "risk_percent": number,
  "risk_level": "string",
  "confidence": number
}

---

## 2. Dashboard Stats
GET /api/dashboard-stats
Response:
{
  "routes_monitored": number,
  "high_risk": number,
  "orders_at_risk": number,
  "avg_delay_days": number,
  "alerts": [
    {
      "route": "string",
      "risk": "string",
      "orders_affected": number,
      "delay_days": number
    }
  ]
}

---

## 3. Cascade Map
GET /api/cascade-map/{route_id}
Response:
{
  "nodes": [
    {
      "id": "string",
      "name": "string",
      "risk_score": number,
      "orders_affected": number,
      "delay_days": number
    }
  ],
  "edges": [
    {
      "from": "string",
      "to": "string"
    }
  ],
  "disrupted_node": "string"
}

---

## 4. Explain Risk
POST /api/explain
Request:
{
  "route_id": "string",
  "risk_data": {}
}
Response:
{
  "why_at_risk": "string",
  "cascade_impact": "string",
  "recommended_actions": "string"
}

---

## 5. Simulate Disruption
POST /api/simulate-disruption
Request:
{
  "route_id": "string",
  "scenario": "string"
}
Response:
{
  "affected_routes": ["string"],
  "total_cost": number,
  "delay_days": number,
  "report": "string"
}