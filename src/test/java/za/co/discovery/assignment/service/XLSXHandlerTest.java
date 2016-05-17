package za.co.discovery.assignment.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import za.co.discovery.assignment.config.ResourceBean;
import za.co.discovery.assignment.entity.Vertex;
import za.co.discovery.assignment.model.EdgeModel;
import za.co.discovery.assignment.model.TrafficModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;

/**
 * Created by Kapeshi.Kongolo on 2016/04/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {XLSXHandler.class, ResourceBean.class},
        loader = AnnotationConfigContextLoader.class)
public class XLSXHandlerTest {
    @Autowired
    private XLSXHandler xlsxHandler;

    @Test
    public void verifyThatReadingVerticesFromFileIsCorrect() throws Exception {
        //Set
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Moon");
        Vertex vertex3 = new Vertex("C", "Jupiter");
        Vertex vertex4 = new Vertex("D", "Venus");
        Vertex vertex5 = new Vertex("E", "Mars");

        Map<String, Vertex> expectedVertexes = new HashMap<>();
        expectedVertexes.put(vertex1.getId(), vertex1);
        expectedVertexes.put(vertex2.getId(), vertex2);
        expectedVertexes.put(vertex3.getId(), vertex3);
        expectedVertexes.put(vertex4.getId(), vertex4);
        expectedVertexes.put(vertex5.getId(), vertex5);

        //Test
        Map<String, Vertex> readVertexes = xlsxHandler.readVertexes();

        //Verify
        assertThat(expectedVertexes, sameBeanAs(readVertexes));
    }

    @Test
    public void verifyThatReadingEdgesFromFileIsCorrect() throws Exception {
        //Set
        EdgeModel edge1 = new EdgeModel("1", "A", "B", 0.44f);
        EdgeModel edge2 = new EdgeModel("2", "A", "C", 1.89f);
        EdgeModel edge3 = new EdgeModel("3", "A", "D", 0.10f);
        EdgeModel edge4 = new EdgeModel("4", "B", "E", 2.44f);
        EdgeModel edge5 = new EdgeModel("5", "C", "E", 3.45f);

        List<EdgeModel> expectedEdges = new ArrayList<>();
        expectedEdges.add(edge1);
        expectedEdges.add(edge2);
        expectedEdges.add(edge3);
        expectedEdges.add(edge4);
        expectedEdges.add(edge5);

        //Test
        List<EdgeModel> readEdges = xlsxHandler.readEdges();

        //Verify
        assertThat(expectedEdges, sameBeanAs(readEdges));
    }

    @Test
    public void verifyThatReadingTrafficsFromFileIsCorrect() throws Exception {
        //Set
        TrafficModel traffic1 = new TrafficModel("1", "A", "B", 0.30f);
        TrafficModel traffic2 = new TrafficModel("2", "A", "C", 0.90f);
        TrafficModel traffic3 = new TrafficModel("3", "A", "D", 0.10f);
        TrafficModel traffic4 = new TrafficModel("4", "B", "E", 0.20f);
        TrafficModel traffic5 = new TrafficModel("5", "C", "E", 1.30f);

        List<TrafficModel> expectedTraffics = new ArrayList<>();
        expectedTraffics.add(traffic1);
        expectedTraffics.add(traffic2);
        expectedTraffics.add(traffic3);
        expectedTraffics.add(traffic4);
        expectedTraffics.add(traffic5);

        //Test
        List<TrafficModel> readTraffics = xlsxHandler.readTraffics();

        //Verify
        assertThat(expectedTraffics, sameBeanAs(readTraffics));
    }

}