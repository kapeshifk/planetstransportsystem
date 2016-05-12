package za.co.discovery.assignment.helper;

import za.co.discovery.assignment.entity.Edge;
import za.co.discovery.assignment.entity.Traffic;
import za.co.discovery.assignment.entity.Vertex;

import java.util.ArrayList;
import java.util.List;

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

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
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
}
