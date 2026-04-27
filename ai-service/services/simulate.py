from services.gemini_service import generate_what_if_analysis

ROUTE_DEPENDENCY_GRAPH = {
    "node_1": ["node_7", "node_8"],
    "node_2": ["node_7"],
    "node_3": ["node_4", "node_8"],
    "node_4": ["node_8", "node_9"],
    "node_7": ["node_8", "node_9"],
    "node_8": ["node_9", "node_10"],
    "node_9": ["node_10"],
    "node_5": ["node_9"],
    "node_6": ["node_10"],
    "node_10": []
}

NODE_NAMES = {
    "node_1": "Chongqing Plant",
    "node_2": "Shenzhen Components",
    "node_3": "Ho Chi Minh Parts",
    "node_4": "Singapore Hub",
    "node_5": "Mumbai Textiles",
    "node_6": "Tokyo Demand",
    "node_7": "Tijuana Assembly",
    "node_8": "Newark DC",
    "node_9": "London Distribution",
    "node_10": "Sydney Retail"
}

COST_PER_NODE = {
    "node_1": 562000, "node_2": 412000, "node_3": 287000,
    "node_4": 198000, "node_7": 445000, "node_8": 312000,
    "node_9": 189000, "node_10": 95000
}

def run_cascade_bfs(disrupted_node: str) -> list:
    visited = set()
    queue = [disrupted_node]
    affected = []
    while queue:
        current = queue.pop(0)
        if current not in visited:
            visited.add(current)
            if current != disrupted_node:
                affected.append(current)
            for neighbor in ROUTE_DEPENDENCY_GRAPH.get(current, []):
                queue.append(neighbor)
    return affected

def simulate_disruption(route_id: str, scenario: str) -> dict:
    disrupted_node = "node_1"
    for node_id, name in NODE_NAMES.items():
        if route_id.lower() in name.lower():
            disrupted_node = node_id
            break

    affected_node_ids = run_cascade_bfs(disrupted_node)
    affected_routes = [NODE_NAMES.get(n, n) for n in affected_node_ids]

    total_cost = COST_PER_NODE.get(disrupted_node, 500000)
    for node in affected_node_ids:
        total_cost += COST_PER_NODE.get(node, 100000) * 0.4

    max_delay = 18
    if scenario.lower() in ["typhoon", "storm"]:
        max_delay = 21
    elif scenario.lower() in ["strike", "port"]:
        max_delay = 14

    report = generate_what_if_analysis(route_id, scenario, affected_routes)

    return {
        "affected_routes": affected_routes,
        "total_cost": round(total_cost, 2),
        "delay_days": max_delay,
        "report": report
    }
