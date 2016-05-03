package za.co.discovery.assignment.model;

import za.co.discovery.assignment.entity.Edge;
import za.co.discovery.assignment.entity.Vertex;

import java.io.Serializable;

public class ShortestPathModel implements Serializable {

    private Vertex sourceVertex;
    private Vertex destinationVertex;
    private Vertex selectedVertex;
    private Edge selectedEdge;
    private String selectedVertexName;
    private String vertexId;
    private String vertexName;
    private String thePath;
    private boolean undirectedGraph;
    private boolean trafficAllowed;

    public Vertex getSelectedVertex() {
        return selectedVertex;
    }

    public void setSelectedVertex(Vertex selectedVertex) {
        this.selectedVertex = selectedVertex;
    }

    public String getVertexId() {
        return vertexId;
    }

    public void setVertexId(String vertexId) {
        this.vertexId = vertexId;
    }

    public String getVertexName() {
        return vertexName;
    }

    public void setVertexName(String vertexName) {
        this.vertexName = vertexName;
    }

    public String getThePath() {
        return thePath;
    }

    public void setThePath(String thePath) {
        this.thePath = thePath;
    }

    public String getSelectedVertexName() {
        return selectedVertexName;
    }

    public void setSelectedVertexName(String selectedVertexName) {
        this.selectedVertexName = selectedVertexName;
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

    public Vertex getSourceVertex() {
        return sourceVertex;
    }

    public void setSourceVertex(Vertex sourceVertex) {
        this.sourceVertex = sourceVertex;
    }

    public Vertex getDestinationVertex() {
        return destinationVertex;
    }

    public void setDestinationVertex(Vertex destinationVertex) {
        this.destinationVertex = destinationVertex;
    }

    public Edge getSelectedEdge() {
        return selectedEdge;
    }

    public void setSelectedEdge(Edge selectedEdge) {
        this.selectedEdge = selectedEdge;
    }
}
