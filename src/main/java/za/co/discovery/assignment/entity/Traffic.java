package za.co.discovery.assignment.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Kapeshi.Kongolo on 2016/04/09.
 */
@Entity(name = "traffic")
public class Traffic implements Serializable {

    @Id
    @SequenceGenerator(name = "trafficSeq", sequenceName = "TRAFFIC_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trafficSeq")
    @Column
    private Long id;

    @Column
    private String routeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "edge_id")
    private Edge route;

    @Column
    private Float delay;

    public Traffic() {
    }

    public Traffic(String routeId, Float delay) {
        this.routeId = routeId;
        this.delay = delay;
    }

    public Traffic(String routeId, Edge route, Float delay) {
        this.routeId = routeId;
        this.route = route;
        this.delay = delay;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Edge getRoute() {
        return route;
    }

    public void setRoute(Edge route) {
        this.route = route;
    }

    public Float getDelay() {
        return delay;
    }

    public void setDelay(Float delay) {
        this.delay = delay;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}

