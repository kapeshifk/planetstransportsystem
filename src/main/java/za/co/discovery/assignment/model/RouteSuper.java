package za.co.discovery.assignment.model;

public class RouteSuper {
    private String Id;
    private String source;
    private String destination;
    private Float weight;

    public RouteSuper() {
    }

    public RouteSuper(String id, String source, String destination, Float weight) {
        Id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }
}
