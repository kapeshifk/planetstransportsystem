package za.co.discovery.assignment.helper;

import za.co.discovery.assignment.entity.Edge;
import za.co.discovery.assignment.entity.Vertex;
import za.co.discovery.assignment.model.EdgeModel;
import za.co.discovery.assignment.model.TrafficModel;

import java.util.Map;

public class GraphMapper {
    private Map<String, Vertex> vertexMap;
    private Map<String, Edge> edgeMap;
    private EdgeModel edgeModel;
    private TrafficModel trafficModel;
    private Vertex source;
    private Vertex destination;
    private Edge edge;

    public GraphMapper() {
    }

    public GraphMapper(Map<String, Vertex> vertexMap, EdgeModel edgeModel) {
        this.vertexMap = vertexMap;
        this.edgeModel = edgeModel;
        mapVertices();
    }

    public GraphMapper(Map<String, Edge> edgeMap, TrafficModel trafficModel) {
        this.edgeMap = edgeMap;
        this.trafficModel = trafficModel;
        mapEdges();
    }

    private void mapVertices() {
        source = vertexMap.get(edgeModel.getSource());
        destination = vertexMap.get(edgeModel.getDestination());
    }

    private void mapEdges() {
        edge = edgeMap.get(createHumanReadableId(trafficModel.getSource(), trafficModel.getDestination()));
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public Edge getEdge() {
        return edge;
    }

    public String createHumanReadableId(String source, String destination) {
        StringBuilder build = new StringBuilder();
        build.append(source);
        build.append("_");
        build.append(destination);
        return build.toString();
    }
}
