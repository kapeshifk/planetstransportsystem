package za.co.discovery.assignment.service;

import org.junit.Test;
import za.co.discovery.assignment.entity.Edge;
import za.co.discovery.assignment.entity.Traffic;
import za.co.discovery.assignment.entity.Vertex;
import za.co.discovery.assignment.helper.Graph;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.Assert.assertThat;


public class ShortestPathServiceTest {

    @Test
    public void verifyThatShortestPathAlgorithmIsCorrect() throws Exception {
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Moon");
        Vertex vertexC = new Vertex("C", "Jupiter");
        Vertex vertexD = new Vertex("D", "Venus");
        Vertex vertexE = new Vertex("E", "Mars");

        List<Vertex> vertices = Arrays.asList(vertexA, vertexB, vertexC, vertexD, vertexE);

        Edge edge1 = new Edge("1", vertexA, vertexB, 1.0f);
        Edge edge2 = new Edge("2", vertexA, vertexC, 1.0f);
        Edge edge3 = new Edge("3", vertexA, vertexD, 1.0f);
        Edge edge4 = new Edge("4", vertexB, vertexE, 1.0f);
        Edge edge5 = new Edge("5", vertexC, vertexE, 1.0f);

        List<Edge> edges = Arrays.asList(edge1, edge2, edge3, edge4, edge5);

        Traffic traffic1 = new Traffic("1", edge1, 5.0f);
        Traffic traffic2 = new Traffic("2", edge2, 5.0f);
        Traffic traffic3 = new Traffic("3", edge3, 5.0f);
        Traffic traffic4 = new Traffic("4", edge4, 15.0f);
        Traffic traffic5 = new Traffic("5", edge5, 5.0f);

        List<Traffic> traffics = Arrays.asList(traffic1, traffic2, traffic3, traffic4, traffic5);

        String expectedPath = "A C E ";
        //Test

        StringBuilder path = new StringBuilder();
        Vertex source = vertices.get(0);
        Vertex destination = vertices.get(vertices.size() - 1);
        Graph graph = new Graph(vertices, edges, traffics);
        graph.setTrafficAllowed(true);
        graph.setUndirectedGraph(true);
        ShortestPathService dijkstra = new ShortestPathService();
        dijkstra.initializePlanets(graph);
        dijkstra.run(source);
        LinkedList<Vertex> paths = dijkstra.getPath(destination);
        if (paths != null) {
            for (Vertex v : paths) {
                path.append(v.getId());
                path.append(" ");
            }
        } else {
            path.append("Not available");
        }


        String actual = path.toString();
        assertThat(expectedPath, sameBeanAs(actual));
    }
}
