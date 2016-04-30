package za.co.discovery.assignment.model;


public class EdgeModel extends RouteSuper {
    public EdgeModel() {
    }

    public EdgeModel(String id, String source, String destination, Float weight) {
        super(id, source, destination, weight);
    }
}
