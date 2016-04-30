package za.co.discovery.assignment.model;

public class TrafficModel extends RouteSuper {

    public TrafficModel() {
    }

    public TrafficModel(String id, String source, String destination, Float weight) {
        super(id, source, destination, weight);
    }
}
