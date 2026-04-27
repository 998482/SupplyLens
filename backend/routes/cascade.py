from fastapi import APIRouter
from schemas import CascadeMapResponse, CascadeNode, CascadeEdge

router = APIRouter()

@router.get("/api/cascade-map/{route_id}", response_model=CascadeMapResponse)
def get_cascade_map(route_id: str):
    nodes = [
        CascadeNode(id="node_1", name="Chongqing Plant", risk_score=94, orders_affected=234, delay_days=18),
        CascadeNode(id="node_2", name="Shenzhen Components", risk_score=72, orders_affected=189, delay_days=14),
        CascadeNode(id="node_3", name="Ho Chi Minh Parts", risk_score=66, orders_affected=156, delay_days=6),
        CascadeNode(id="node_4", name="Singapore Hub", risk_score=55, orders_affected=98, delay_days=4),
        CascadeNode(id="node_5", name="Mumbai Textiles", risk_score=45, orders_affected=67, delay_days=3),
        CascadeNode(id="node_6", name="Tokyo Demand", risk_score=38, orders_affected=43, delay_days=1),
        CascadeNode(id="node_7", name="Tijuana Assembly", risk_score=72, orders_affected=189, delay_days=12),
        CascadeNode(id="node_8", name="Newark DC", risk_score=60, orders_affected=145, delay_days=8),
        CascadeNode(id="node_9", name="London Distribution", risk_score=40, orders_affected=89, delay_days=3),
        CascadeNode(id="node_10", name="Sydney Retail", risk_score=25, orders_affected=34, delay_days=1),
    ]
    edges = [
        CascadeEdge(**{"from": "node_1", "to": "node_7"}),
        CascadeEdge(**{"from": "node_2", "to": "node_7"}),
        CascadeEdge(**{"from": "node_3", "to": "node_4"}),
        CascadeEdge(**{"from": "node_4", "to": "node_8"}),
        CascadeEdge(**{"from": "node_7", "to": "node_8"}),
        CascadeEdge(**{"from": "node_8", "to": "node_9"}),
        CascadeEdge(**{"from": "node_5", "to": "node_9"}),
        CascadeEdge(**{"from": "node_6", "to": "node_10"}),
        CascadeEdge(**{"from": "node_9", "to": "node_10"}),
    ]
    return CascadeMapResponse(nodes=nodes, edges=edges, disrupted_node="node_1")
