package za.co.discovery.assignment.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by Kapeshi.Kongolo on 2016/04/09.
 */
@Entity(name = "vertex")
@Table
public class Vertex implements Serializable {

    @Id
    @Column
    private String vertexId;
    @Column
    private String name;

    public Vertex() {
    }

    public Vertex(String vertexId, String name) {
        this.vertexId = vertexId;
        this.name = name;
    }

    public String getVertexId() {
        return vertexId;
    }

    public void setVertexId(String vertexId) {
        this.vertexId = vertexId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex vertex = (Vertex) o;

        return vertexId != null ?
                vertexId.equals(vertex.vertexId) :
                vertex.vertexId == null && (name != null ? name.equals(vertex.name) : vertex.name == null);

    }

    @Override
    public int hashCode() {
        int result = vertexId != null ? vertexId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}