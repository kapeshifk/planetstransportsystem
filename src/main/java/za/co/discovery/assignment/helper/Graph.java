package za.co.discovery.assignment.helper;

import za.co.discovery.assignment.entity.Edge;
import za.co.discovery.assignment.entity.Traffic;
import za.co.discovery.assignment.entity.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kapeshi.Kongolo on 2016/04/09.
 */
public class Graph {

    private List<Vertex> vertexes;
    private List<Edge> edges;
    private List<Traffic> traffics;
    private boolean undirectedGraph;
    private boolean trafficAllowed;

    public Graph(List<Vertex> vertexes, List<Edge> edges, List<Traffic> traffics) {
        this.vertexes = vertexes;
        this.edges = edges;
        this.traffics = new ArrayList<>(traffics);
    }

    public List<Traffic> getTraffics() {
        return traffics;
    }

    public List<Vertex> getVertexes() {
        return vertexes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public boolean isUndirectedGraph() {
        return undirectedGraph;
    }

    public void setUndirectedGraph(boolean undirectedGraph) {
        this.undirectedGraph = undirectedGraph;
    }

    public boolean isTrafficAllowed() {
        return trafficAllowed;
    }

    public void setTrafficAllowed(boolean trafficAllowed) {
        this.trafficAllowed = trafficAllowed;
    }

    public void processTraffics() {
        for (Traffic traffic : traffics) {
            edges.stream().filter(edge -> edge.equals(traffic.getRoute())).forEach(edge -> {
                System.out.println("DISTANCE " + edge.getDistance());
                System.out.println("DELAY " + traffic.getDelay());
                Float actualDistance = edge.getDistance() + traffic.getDelay();
                edge.setDistance(actualDistance);
            });
        }
    }

    public List<Edge> getUndirectedEdges() {
        List<Edge> undirectedEdges = new ArrayList();
        for (Edge fromEdge : edges) {
            Edge toEdge = copyAdjacentEdge(fromEdge);
            undirectedEdges.add(fromEdge);
            undirectedEdges.add(toEdge);
        }
        return undirectedEdges;
    }

    public Edge copyAdjacentEdge(Edge fromEdge) {
        Edge toEdge = new Edge();
        toEdge.setRouteId(fromEdge.getRouteId());
        toEdge.setSource(fromEdge.getDestination());
        toEdge.setDestination(fromEdge.getSource());
        toEdge.setDistance(fromEdge.getDistance());
        return toEdge;
    }
}
