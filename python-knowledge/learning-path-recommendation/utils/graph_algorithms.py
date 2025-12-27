# 图算法工具

import heapq
import math


class GraphNode:
    """图节点类"""
    
    def __init__(self, node_id, data=None):
        """
        初始化图节点
        :param node_id: 节点ID
        :param data: 节点数据
        """
        self.node_id = node_id
        self.data = data
        self.neighbors = {}
    
    def add_neighbor(self, neighbor_id, weight=1.0):
        """
        添加邻居节点
        :param neighbor_id: 邻居节点ID
        :param weight: 边权重
        """
        self.neighbors[neighbor_id] = weight


class Graph:
    """图类"""
    
    def __init__(self):
        """
        初始化图
        """
        self.nodes = {}
    
    def add_node(self, node_id, data=None):
        """
        添加节点
        :param node_id: 节点ID
        :param data: 节点数据
        """
        if node_id not in self.nodes:
            self.nodes[node_id] = GraphNode(node_id, data)
    
    def add_edge(self, from_node_id, to_node_id, weight=1.0, bidirectional=False):
        """
        添加边
        :param from_node_id: 起始节点ID
        :param to_node_id: 目标节点ID
        :param weight: 边权重
        :param bidirectional: 是否为双向边
        """
        # 确保节点存在
        self.add_node(from_node_id)
        self.add_node(to_node_id)
        
        # 添加边
        self.nodes[from_node_id].add_neighbor(to_node_id, weight)
        
        # 如果是双向边，添加反向边
        if bidirectional:
            self.nodes[to_node_id].add_neighbor(from_node_id, weight)


def dijkstra(graph, start_node_id, end_node_id=None):
    """
    Dijkstra算法实现
    :param graph: 图对象
    :param start_node_id: 起始节点ID
    :param end_node_id: 目标节点ID（可选）
    :return: 从起始节点到所有节点或特定节点的最短路径
    """
    if start_node_id not in graph.nodes:
        raise ValueError(f"起始节点 {start_node_id} 不存在")
    
    if end_node_id and end_node_id not in graph.nodes:
        raise ValueError(f"目标节点 {end_node_id} 不存在")
    
    # 距离字典，记录从起始节点到各节点的最短距离
    distances = {node_id: float('infinity') for node_id in graph.nodes}
    distances[start_node_id] = 0
    
    # 前驱节点字典，记录路径
    predecessors = {node_id: None for node_id in graph.nodes}
    
    # 优先队列，用于选择当前距离最小的节点
    priority_queue = []
    heapq.heappush(priority_queue, (0, start_node_id))
    
    # 已访问节点集合
    visited = set()
    
    while priority_queue:
        current_distance, current_node_id = heapq.heappop(priority_queue)
        
        # 如果到达目标节点，提前终止
        if current_node_id == end_node_id:
            break
        
        # 如果当前节点已访问，跳过
        if current_node_id in visited:
            continue
        
        visited.add(current_node_id)
        
        # 遍历当前节点的所有邻居
        current_node = graph.nodes[current_node_id]
        for neighbor_id, weight in current_node.neighbors.items():
            if neighbor_id in visited:
                continue
                
            # 计算新的距离
            distance = current_distance + weight
            
            # 如果新距离更小，更新
            if distance < distances[neighbor_id]:
                distances[neighbor_id] = distance
                predecessors[neighbor_id] = current_node_id
                heapq.heappush(priority_queue, (distance, neighbor_id))
    
    # 如果指定了目标节点，返回路径
    if end_node_id:
        path = []
        current = end_node_id
        while current is not None:
            path.append(current)
            current = predecessors[current]
        path.reverse()
        return path, distances[end_node_id]
    
    # 否则返回所有节点的距离和前驱
    return distances, predecessors


def a_star(graph, start_node_id, end_node_id, heuristic=None):
    """
    A*算法实现
    :param graph: 图对象
    :param start_node_id: 起始节点ID
    :param end_node_id: 目标节点ID
    :param heuristic: 启发函数，默认为欧几里得距离
    :return: 从起始节点到目标节点的最短路径
    """
    if start_node_id not in graph.nodes:
        raise ValueError(f"起始节点 {start_node_id} 不存在")
    
    if end_node_id not in graph.nodes:
        raise ValueError(f"目标节点 {end_node_id} 不存在")
    
    # 默认启发函数：欧几里得距离（如果节点数据包含坐标）
    if heuristic is None:
        def default_heuristic(node_id):
            node = graph.nodes[node_id]
            end_node = graph.nodes[end_node_id]
            
            # 如果节点有坐标数据，使用欧几里得距离
            if hasattr(node.data, 'x') and hasattr(node.data, 'y'):
                return math.sqrt(
                    (node.data.x - end_node.data.x) ** 2 + 
                    (node.data.y - end_node.data.y) ** 2
                )
            # 否则使用曼哈顿距离或默认值
            elif hasattr(node.data, 'difficulty') and hasattr(end_node.data, 'difficulty'):
                return abs(node.data.difficulty - end_node.data.difficulty)
            # 否则使用默认值
            return 0
        
        heuristic = default_heuristic
    
    # 距离字典，记录从起始节点到各节点的实际距离
    g_scores = {node_id: float('infinity') for node_id in graph.nodes}
    g_scores[start_node_id] = 0
    
    # 距离字典，记录从起始节点到目标节点的估计距离
    f_scores = {node_id: float('infinity') for node_id in graph.nodes}
    f_scores[start_node_id] = heuristic(start_node_id)
    
    # 前驱节点字典，记录路径
    predecessors = {node_id: None for node_id in graph.nodes}
    
    # 优先队列，用于选择当前f_score最小的节点
    priority_queue = []
    heapq.heappush(priority_queue, (f_scores[start_node_id], start_node_id))
    
    # 已访问节点集合
    visited = set()
    
    while priority_queue:
        current_f_score, current_node_id = heapq.heappop(priority_queue)
        
        # 如果到达目标节点，提前终止
        if current_node_id == end_node_id:
            break
        
        # 如果当前节点已访问，跳过
        if current_node_id in visited:
            continue
        
        visited.add(current_node_id)
        
        # 遍历当前节点的所有邻居
        current_node = graph.nodes[current_node_id]
        for neighbor_id, weight in current_node.neighbors.items():
            if neighbor_id in visited:
                continue
                
            # 计算新的实际距离
            tentative_g_score = g_scores[current_node_id] + weight
            
            # 如果新距离更小，更新
            if tentative_g_score < g_scores[neighbor_id]:
                predecessors[neighbor_id] = current_node_id
                g_scores[neighbor_id] = tentative_g_score
                f_scores[neighbor_id] = tentative_g_score + heuristic(neighbor_id)
                heapq.heappush(priority_queue, (f_scores[neighbor_id], neighbor_id))
    
    # 构建路径
    path = []
    current = end_node_id
    while current is not None:
        path.append(current)
        current = predecessors[current]
    path.reverse()
    
    return path, g_scores[end_node_id]


def find_all_paths(graph, start_node_id, end_node_id, max_depth=10):
    """
    查找从起始节点到目标节点的所有路径
    :param graph: 图对象
    :param start_node_id: 起始节点ID
    :param end_node_id: 目标节点ID
    :param max_depth: 最大路径长度
    :return: 所有路径的列表
    """
    if start_node_id not in graph.nodes or end_node_id not in graph.nodes:
        return []
    
    paths = []
    visited = set()
    
    def dfs(current_node_id, path, depth):
        """
        深度优先搜索
        :param current_node_id: 当前节点ID
        :param path: 当前路径
        :param depth: 当前深度
        """
        if depth > max_depth:
            return
        
        path.append(current_node_id)
        visited.add(current_node_id)
        
        if current_node_id == end_node_id:
            paths.append(path.copy())
        else:
            current_node = graph.nodes[current_node_id]
            for neighbor_id in current_node.neighbors:
                if neighbor_id not in visited:
                    dfs(neighbor_id, path, depth + 1)
        
        path.pop()
        visited.remove(current_node_id)
    
    dfs(start_node_id, [], 0)
    return paths


def calculate_path_cost(graph, path):
    """
    计算路径的总成本
    :param graph: 图对象
    :param path: 路径列表
    :return: 路径总成本
    """
    if len(path) < 2:
        return 0
    
    cost = 0
    for i in range(len(path) - 1):
        from_node = path[i]
        to_node = path[i + 1]
        
        if from_node not in graph.nodes or to_node not in graph.nodes[from_node].neighbors:
            raise ValueError(f"路径 {path} 包含无效的边")
        
        cost += graph.nodes[from_node].neighbors[to_node]
    
    return cost


def filter_paths_by_cost(paths, graph, max_cost=None):
    """
    根据成本过滤路径
    :param paths: 路径列表
    :param graph: 图对象
    :param max_cost: 最大允许成本
    :return: 过滤后的路径列表
    """
    if max_cost is None:
        return paths
    
    filtered = []
    for path in paths:
        cost = calculate_path_cost(graph, path)
        if cost <= max_cost:
            filtered.append((path, cost))
    
    # 按成本排序
    filtered.sort(key=lambda x: x[1])
    return [path for path, cost in filtered]