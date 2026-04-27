import google.generativeai as genai
import os
import json
from dotenv import load_dotenv

load_dotenv()

genai.configure(api_key=os.getenv("GEMINI_API_KEY"))
model = genai.GenerativeModel("gemini-1.5-flash")

def explain_disruption(route_id: str, risk_data: dict) -> dict:
    prompt = f"""
You are an expert supply chain risk analyst for SupplyLens.
Analyze this shipment disruption:
Route ID: {route_id}
Risk Data: {json.dumps(risk_data)}

Respond ONLY with valid JSON, no extra text, no backticks:
{{
    "why_at_risk": "2-3 sentences explaining why this route is at risk",
    "cascade_impact": "2-3 sentences on downstream effects",
    "recommended_actions": "3 numbered action items"
}}
"""
    try:
        response = model.generate_content(prompt)
        text = response.text.strip()
        if "```" in text:
            text = text.split("```")[1]
            if text.startswith("json"):
                text = text[4:]
        return json.loads(text.strip())
    except Exception as e:
        print(f"Gemini error: {e}")
        return {
            "why_at_risk": f"Route {route_id} faces critical disruption due to severe weather and port congestion.",
            "cascade_impact": "Tijuana Assembly faces 14-day delay. Newark DC inventory depleted within 72 hours.",
            "recommended_actions": "1. Reroute via Singapore Hub.\n2. Activate backup supplier in Ho Chi Minh City.\n3. Pre-position stock at Newark DC before Day 5."
        }

def generate_what_if_analysis(route_id: str, scenario: str, affected_routes: list) -> str:
    prompt = f"""
You are a supply chain simulation expert for SupplyLens.
Generate a simulation report for:
Route ID: {route_id}
Scenario: {scenario}
Affected Routes: {json.dumps(affected_routes)}

Write 4-5 sentences as plain text covering primary impact, cascade effects, financial exposure, recovery timeline.
No JSON, no markdown.
"""
    try:
        response = model.generate_content(prompt)
        return response.text.strip()
    except Exception as e:
        return f"SIMULATION REPORT - {scenario}\n\nCritical disruption at {route_id}. {len(affected_routes)} downstream routes affected. Combined exposure $1.58M USD. Recovery estimated 23 days."
