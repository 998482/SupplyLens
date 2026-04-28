def get_supply_graph():
    nodes = {
        "N1": {"id": "N1", "name": "Chongqing Plant", "route": "China", "orders_affected": 1200, "delay_days": 3, "risk_score": 0.8},
        "N2": {"id": "N2", "name": "Shenzhen Components", "route": "China", "orders_affected": 900, "delay_days": 2, "risk_score": 0.7},
        "N3": {"id": "N3", "name": "Ho Chi Minh Parts", "route": "Vietnam", "orders_affected": 800, "delay_days": 4, "risk_score": 0.75},
        "N4": {"id": "N4", "name": "Singapore Transshipment", "route": "Singapore", "orders_affected": 1500, "delay_days": 5, "risk_score": 0.9},
        "N5": {"id": "N5", "name": "Mumbai Textiles", "route": "India", "orders_affected": 600, "delay_days": 3, "risk_score": 0.65},
        "N6": {"id": "N6", "name": "Tokyo Demand Semiconductor", "route": "Japan", "orders_affected": 1100, "delay_days": 2, "risk_score": 0.85},
        "N7": {"id": "N7", "name": "Tijuana Assembly", "route": "Mexico", "orders_affected": 1300, "delay_days": 4, "risk_score": 0.88},
        "N8": {"id": "N8", "name": "Newark DC", "route": "USA", "orders_affected": 1400, "delay_days": 3, "risk_score": 0.8},
        "N9": {"id": "N9", "name": "London Distribution", "route": "UK", "orders_affected": 1000, "delay_days": 2, "risk_score": 0.7},
        "N10": {"id": "N10", "name": "Sydney Retail", "route": "Australia", "orders_affected": 700, "delay_days": 5, "risk_score": 0.78},
        "N11": {"id": "N11", "name": "Dubai Hub", "route": "UAE", "orders_affected": 950, "delay_days": 3, "risk_score": 0.82},
        "N12": {"id": "N12", "name": "Hamburg Port", "route": "Germany", "orders_affected": 1050, "delay_days": 4, "risk_score": 0.83},
        "N13": {"id": "N13", "name": "Los Angeles Port", "route": "USA", "orders_affected": 1250, "delay_days": 5, "risk_score": 0.9},
        "N14": {"id": "N14", "name": "São Paulo Distribution", "route": "Brazil", "orders_affected": 850, "delay_days": 6, "risk_score": 0.77},
        "N15": {"id": "N15", "name": "Johannesburg Hub", "route": "South Africa", "orders_affected": 750, "delay_days": 4, "risk_score": 0.74},
        "N16": {"id": "N16", "name": "Paris Retail", "route": "France", "orders_affected": 650, "delay_days": 2, "risk_score": 0.68},
        "N17": {"id": "N17", "name": "Toronto DC", "route": "Canada", "orders_affected": 900, "delay_days": 3, "risk_score": 0.79},
    }

    edges = [
        ("N2", "N1"), ("N3", "N1"), ("N1", "N4"), ("N5", "N4"),
        ("N4", "N6"), ("N4", "N7"), ("N6", "N8"), ("N7", "N8"),
        ("N8", "N9"), ("N9", "N10"), ("N4", "N11"), ("N11", "N12"),
        ("N12", "N9"), ("N13", "N8"), ("N8", "N14"), ("N11", "N15"),
        ("N9", "N16"), ("N8", "N17"),
    ]

    return {"nodes": nodes, "edges": edges}