package za.co.discovery.assignment.service;

import org.springframework.stereotype.Service;
import za.co.discovery.assignment.entity.Edge;
import za.co.discovery.assignment.entity.Traffic;
import za.co.discovery.assignment.entity.Vertex;
import za.co.discovery.assignment.helper.Graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PathImpl {

    public Graph overlayGraph(Graph graph) {
        List<Edge> edges = new ArrayList<>(graph.getEdges());
        List<Traffic> traffics = new ArrayList<>(graph.getTraffics());
        if (graph.isTrafficAllowed()) {
            edges = processTraffics(edges, traffics);
        }
        if (graph.isUndirectedGraph()) {
            edges = getUndirectedEdges(edges);
        }
        graph.setEdges(edges);
        return graph;
    }

    private List<Edge> processTraffics(List<Edge> edges, List<Traffic> traffics) {
        for (Traffic traffic : traffics) {
            edges.stream().filter(edge -> edge.equals(traffic.getRoute())).forEach(edge -> {
                Float actualDistance = edge.getDistance() + traffic.getDelay();
                edge.setDistance(actualDistance);
            });
        }
        return edges;
    }

    private List<Edge> getUndirectedEdges(List<Edge> edges) {
        List<Edge> undirectedEdges = new ArrayList();
        for (Edge fromEdge : edges) {
            Edge toEdge = copyAdjacentEdge(fromEdge);
            undirectedEdges.add(fromEdge);
            undirectedEdges.add(toEdge);
        }
        return undirectedEdges;
    }

    private Edge copyAdjacentEdge(Edge fromEdge) {
        Edge toEdge = new Edge();
        toEdge.setRouteId(fromEdge.getRouteId());
        toEdge.setSource(fromEdge.getDestination());
        toEdge.setDestination(fromEdge.getSource());
        toEdge.setDistance(fromEdge.getDistance());
        return toEdge;
    }

    public Vertex getVertexWithLowestDistance(Map<Vertex, Float> distance, Set<Vertex> vertexes) {
        Vertex lowestVertex = null;
        for (Vertex vertex : vertexes) {
            if (lowestVertex == null) {
                lowestVertex = vertex;
            } else if (getShortestDistance(distance, vertex) < getShortestDistance(distance, lowestVertex)) {
                lowestVertex = vertex;
            }
        }
        return lowestVertex;
    }

    public Float getShortestDistance(Map<Vertex, Float> distance, Vertex destination) {
        Float d = distance.get(destination);
        if (d == null) {
            return Float.POSITIVE_INFINITY;
        } else {
            return d;
        }
    }

    public float getDistance(List<Edge> edges, Vertex source, Vertex target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(source) && edge.getDestination().equals(target)) {
                return edge.getDistance();
            }
        }
        throw new RuntimeException("Error: Something went wrong!");
    }

    public List<Vertex> getNeighbors(List<Edge> edges, Set<Vertex> visitedVertices, Vertex currentVertex) {
        List<Vertex> neighbors = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(currentVertex) && !isVisited(visitedVertices, edge.getDestination())) {
                neighbors.add(edge.getDestination());
            }
        }
        return neighbors;
    }

    private boolean isVisited(Set<Vertex> visitedVertices, Vertex vertex) {
        return visitedVertices.contains(vertex);
    }
}
