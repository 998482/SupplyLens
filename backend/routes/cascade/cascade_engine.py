from collections import deque, defaultdict

class CascadeEngine:
    def __init__(self, nodes: dict, edges: list):
        self.nodes = nodes
        self.edges = edges
        self.graph = defaultdict(list)
        for src, dest in edges:
            self.graph[src].append(dest)

    def get_cascade(self, disrupted_node_id):
        visited = set()
        queue = deque()
        queue.append((disrupted_node_id, 0))
        visited.add(disrupted_node_id)
        cascade = defaultdict(list)

        while queue:
            current_node, level = queue.popleft()
            for neighbor in self.graph[current_node]:
                if neighbor not in visited:
                    visited.add(neighbor)
                    tier = level + 1
                    cascade[tier].append(self.nodes[neighbor])
                    queue.append((neighbor, tier))

        return dict(cascade)