package za.co.discovery.assignment.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kapeshi.Kongolo on 2016/04/09.
 */
@Entity(name = "vertex")
public class Vertex implements Serializable {

    @Id
    @Column
    private String id;
    @Column
    private String name;
    @OneToMany(mappedBy = "source", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Edge> sourceEdges = new ArrayList<>();

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Edge> destinationEdges = new ArrayList<>();

    public Vertex() {
    }

    public Vertex(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Edge> getSourceEdges() {
        return sourceEdges;
    }

    public void addSourceEdges(Edge edge) {
        edge.setSource(this);
        sourceEdges.add(edge);
    }

    public void removeSourceEdges(Edge edge) {
        sourceEdges.remove(edge);
        if (edge != null) {
            edge.setSource(null);
        }
    }

    public List<Edge> getDestinationEdges() {
        return destinationEdges;
    }

    public void addDestinationEdges(Edge edge) {
        edge.setDestination(this);
        destinationEdges.add(edge);
    }

    public void removeDestinationEdges(Edge edge) {
        destinationEdges.remove(edge);
        if (edge != null) {
            edge.setDestination(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex vertex = (Vertex) o;

        return id != null ?
                id.equals(vertex.id) :
                vertex.id == null && (name != null ? name.equals(vertex.name) : vertex.name == null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}