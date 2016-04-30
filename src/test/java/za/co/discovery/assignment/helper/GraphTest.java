package za.co.discovery.assignment.helper;

import org.junit.Before;
import org.junit.Test;
import za.co.discovery.assignment.entity.Edge;
import za.co.discovery.assignment.entity.Traffic;
import za.co.discovery.assignment.entity.Vertex;

import java.util.ArrayList;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.Assert.assertEquals;

public class GraphTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void verifyThatTrafficOverlayOnGraphIsCorrect() throws Exception {
        //Set
        List<Vertex> vertices = new ArrayList<>();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex vertex3 = new Vertex("C", "Venus");

        Edge edge1 = new Edge("1", vertex1, vertex2, 1.5f);
        Edge edge2 = new Edge("2", vertex2, vertex3, 2.5f);
        Edge edge3 = new Edge("3", vertex1, vertex3, 3.5f);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);

        Traffic traffic1 = new Traffic("1", edge1, 0.5f);
        Traffic traffic2 = new Traffic("2", edge2, 1.0f);
        Traffic traffic3 = new Traffic("3", edge3, 1.5f);

        List<Traffic> traffics = new ArrayList<>();
        traffics.add(traffic1);
        traffics.add(traffic2);
        traffics.add(traffic3);

        Edge edgeExpected1 = new Edge("1", vertex1, vertex2, 2.0f);
        Edge edgeExpected2 = new Edge("2", vertex2, vertex3, 3.5f);
        Edge edgeExpected3 = new Edge("3", vertex1, vertex3, 5.0f);
        List<Edge> edgesExpected = new ArrayList<>();
        edgesExpected.add(edgeExpected1);
        edgesExpected.add(edgeExpected2);
        edgesExpected.add(edgeExpected3);
        boolean expectedTraffic = true;
        Graph expectedGraph = new Graph(vertices, edgesExpected, traffics);
        expectedGraph.setTrafficAllowed(expectedTraffic);


        //Test
        Graph actualGraph = new Graph(vertices, edges, traffics);
        actualGraph.setTrafficAllowed(true);
        actualGraph.processTraffics();
        boolean actualTraffic = actualGraph.isTrafficAllowed();

        List<Vertex> verticesExpected = expectedGraph.getVertexes();
        List<Traffic> trafficsExpected = expectedGraph.getTraffics();
        //Verify
        assertThat(actualGraph, sameBeanAs(expectedGraph));
        assertThat(actualGraph, sameBeanAs(expectedGraph));
        assertThat(vertices, sameBeanAs(verticesExpected));
        assertThat(traffics, sameBeanAs(trafficsExpected));
        assertThat(actualTraffic, sameBeanAs(expectedTraffic));
    }

    @Test
    public void verifyThatUndirectedEdgesOnGraphIsCorrect() throws Exception {
        //Set
        List<Vertex> vertices = new ArrayList<>();
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Mars");
        Vertex vertexC = new Vertex("C", "Venus");
        Vertex vertexD = new Vertex("D", "Dream Team");

        Edge edge1 = new Edge("1", vertexA, vertexB, 0.44f);
        Edge edge2 = new Edge("2", vertexA, vertexC, 1.89f);
        Edge edge3 = new Edge("3", vertexA, vertexD, 0.10f);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);

        Traffic traffic1 = new Traffic("1", edge1, 0.30f);
        Traffic traffic2 = new Traffic("2", edge2, 0.90f);
        Traffic traffic3 = new Traffic("3", edge3, 0.10f);

        List<Traffic> traffics = new ArrayList<>();
        traffics.add(traffic1);
        traffics.add(traffic2);
        traffics.add(traffic3);

        boolean expectedUndirected = true;

        //Test
        Graph graph = new Graph(vertices, edges, traffics);
        graph.setUndirectedGraph(true);
        List<Edge> actualEdges = graph.getUndirectedEdges();
        boolean actualUndirected = graph.isUndirectedGraph();

        Graph actualGraph = new Graph(vertices, actualEdges, traffics);


        Edge edgeExpected1 = new Edge("1", vertexA, vertexB, 0.44f);
        Edge edgeExpected2 = new Edge("1", vertexB, vertexA, 0.44f);
        Edge edgeExpected3 = new Edge("2", vertexA, vertexC, 1.89f);
        Edge edgeExpected4 = new Edge("2", vertexC, vertexA, 1.89f);
        Edge edgeExpected5 = new Edge("3", vertexA, vertexD, 0.10f);
        Edge edgeExpected6 = new Edge("3", vertexD, vertexA, 0.10f);
        List<Edge> edgesExpected = new ArrayList<>();
        edgesExpected.add(edgeExpected1);
        edgesExpected.add(edgeExpected2);
        edgesExpected.add(edgeExpected3);
        edgesExpected.add(edgeExpected4);
        edgesExpected.add(edgeExpected5);
        edgesExpected.add(edgeExpected6);

        Graph expectedGraph = new Graph(vertices, edgesExpected, traffics);

        //Verify
        assertThat(actualEdges, sameBeanAs(edgesExpected));
        assertThat(actualGraph, sameBeanAs(expectedGraph));
        assertEquals(actualUndirected, expectedUndirected);
    }
}
