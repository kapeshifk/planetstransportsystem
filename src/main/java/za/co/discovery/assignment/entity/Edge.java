package za.co.discovery.assignment.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Kapeshi.Kongolo on 2016/04/09.
 */
@Entity(name = "edge")
public class Edge implements Serializable {

    @OneToOne(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Traffic traffic;
    @Id
    @SequenceGenerator(name = "edgeSeq", sequenceName = "EDGE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "edgeSeq")
    @Column
    private Long id;
    @Column
    private String routeId;
    @ManyToOne
    private Vertex source;
    @ManyToOne
    private Vertex destination;
    @Column
    private Float distance;

    public Edge() {
    }

    public Edge(String routeId, Float distance) {
        this.routeId = routeId;
        this.distance = distance;
    }

    public Edge(String routeId, Vertex source, Vertex destination, Float distance) {
        this.routeId = routeId;
        this.source = source;
        this.destination = destination;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public Vertex getSource() {
        return source;
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public void setDestination(Vertex destination) {
        this.destination = destination;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Traffic getTraffic() {
        return traffic;
    }

    public void setTraffic(Traffic traffic) {
        this.traffic = traffic;
    }

    public void addTraffic(Traffic traffic) {
        traffic.setRoute(this);
        this.traffic = traffic;
    }

    public void removeTraffic() {
        if (traffic != null) {
            traffic.setRoute(null);
            this.traffic = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge edge = (Edge) o;

        if (routeId != null ? !routeId.equals(edge.routeId) : edge.routeId != null) return false;
        if (source != null ? !source.equals(edge.source) : edge.source != null) return false;
        return destination != null ? destination.equals(edge.destination) : edge.destination == null;

    }

    @Override
    public int hashCode() {
        int result = routeId != null ? routeId.hashCode() : 0;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        result = 31 * result + (destination != null ? destination.hashCode() : 0);
        return result;
    }
}

