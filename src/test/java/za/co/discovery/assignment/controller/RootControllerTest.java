package za.co.discovery.assignment.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import za.co.discovery.assignment.entity.Edge;
import za.co.discovery.assignment.entity.Traffic;
import za.co.discovery.assignment.entity.Vertex;
import za.co.discovery.assignment.helper.Graph;
import za.co.discovery.assignment.model.ShortestPathModel;
import za.co.discovery.assignment.service.EntityManagerService;
import za.co.discovery.assignment.service.ShortestPathService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Created by Kapeshi.Kongolo on 2016/04/18.
 */
public class RootControllerTest {
    @Mock
    View mockView;
    @InjectMocks
    private RootController controller;
    @Mock
    private EntityManagerService entityManagerService;
    @Mock
    private ShortestPathService shortestPathService;
    private List<Vertex> vertices;
    private List<Edge> edges;
    private List<Traffic> traffics;
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Moon");
        Vertex vertex3 = new Vertex("C", "Jupiter");
        Vertex vertex4 = new Vertex("D", "Venus");
        Vertex vertex5 = new Vertex("E", "Mars");

        vertices = new ArrayList<>();
        vertices.add(vertex1);
        vertices.add(vertex2);
        vertices.add(vertex3);
        vertices.add(vertex4);
        vertices.add(vertex5);

        Edge edge1 = new Edge(1, "1", "A", "B", 0.44f);
        Edge edge2 = new Edge(2, "2", "A", "C", 1.89f);
        Edge edge3 = new Edge(3, "3", "A", "D", 0.10f);
        Edge edge4 = new Edge(4, "4", "B", "H", 2.44f);
        Edge edge5 = new Edge(5, "5", "B", "E", 3.45f);

        edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        Traffic traffic1 = new Traffic("1", "A", "B", 0.30f);
        Traffic traffic2 = new Traffic("2", "A", "C", 0.90f);
        Traffic traffic3 = new Traffic("3", "A", "D", 0.10f);
        Traffic traffic4 = new Traffic("4", "B", "H", 0.20f);
        Traffic traffic5 = new Traffic("5", "B", "E", 1.30f);

        traffics = new ArrayList<>();
        traffics.add(traffic1);
        traffics.add(traffic2);
        traffics.add(traffic3);
        traffics.add(traffic4);
        traffics.add(traffic5);
        MockitoAnnotations.initMocks(this);
        mockMvc = standaloneSetup(controller)
                .setSingleView(mockView)
                .build();

    }

    @Test
    public void verifyThatListVerticesViewAndModelIsCorrect() throws Exception {
        //Set
        when(entityManagerService.getAllVertices()).thenReturn(vertices);
        setUpFixture();
        //Verify
        mockMvc.perform(get("/vertices"))
                .andExpect(model().attribute("vertices", sameBeanAs(vertices)))
                .andExpect(view().name("vertices"));
    }

    @Test
    public void verifyThatShowVertexViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex expectedVertex = new Vertex("A", "Earth");
        when(entityManagerService.getVertexById("vertexId")).thenReturn(expectedVertex);
        //Verify
        mockMvc.perform(get("/vertex/vertexId"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("vertex", sameBeanAs(expectedVertex)))
                .andExpect(view().name("vertexshow"));
    }

    @Test
    public void verifyThatAddVertexViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex expectedVertex = new Vertex();
        //Verify
        mockMvc.perform(get("/vertex/new"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("vertex", sameBeanAs(expectedVertex)))
                .andExpect(view().name("vertexadd"));
    }

    @Test
    public void verifyThatSaveVertexViewIsCorrect() throws Exception {
        //Set
        Vertex expectedVertex = new Vertex("A", "Earth");
        when(entityManagerService.vertexExist("A")).thenReturn(false);
        when(entityManagerService.saveVertex(expectedVertex)).thenReturn(expectedVertex);

        //Test
        mockMvc.perform(post("/vertex").param("vertexId", "A").param("name", "Earth"))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/vertex/" + expectedVertex.getVertexId()));

        //Verify
        ArgumentCaptor<Vertex> formObjectArgument = ArgumentCaptor.forClass(Vertex.class);
        verify(entityManagerService, times(1)).saveVertex(formObjectArgument.capture());

        Vertex formObject = formObjectArgument.getValue();
        assertThat(formObjectArgument.getValue(), is(sameBeanAs(expectedVertex)));

        assertThat(formObject.getVertexId(), is("A"));
        assertThat(formObject.getName(), is("Earth"));
    }

    @Test
    public void verifyThatSaveExistingVertexViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex expectedVertex = new Vertex("A", "Earth");
        when(entityManagerService.vertexExist("A")).thenReturn(true);
        when(entityManagerService.getVertexById("A")).thenReturn(expectedVertex);
        String message = "Planet A already exists as Earth";
        //Verify
        mockMvc.perform(post("/vertex").param("vertexId", "A").param("name", "Earth"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
    }

    @Test
    public void verifyThatEditVertexViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex expectedVertex = new Vertex("A", "Earth");
        when(entityManagerService.getVertexById("vertexId")).thenReturn(expectedVertex);
        //Verify
        mockMvc.perform(get("/vertex/edit/vertexId"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("vertex", sameBeanAs(expectedVertex)))
                .andExpect(view().name("vertexupdate"));
    }

    @Test
    public void verifyThatUpdateVertexViewIsCorrect() throws Exception {
        //Set
        Vertex expectedVertex = new Vertex("A", "Earth");
        when(entityManagerService.updateVertex(expectedVertex)).thenReturn(expectedVertex);
        //Verify
        mockMvc.perform(post("/vertexupdate").param("vertexId", "A").param("name", "Earth"))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/vertex/" + expectedVertex.getVertexId()));
    }

    @Test
    public void verifyThatDeleteVertexViewIsCorrect() throws Exception {
        //Set
        when(entityManagerService.deleteVertex("vertexId")).thenReturn(true);
        //Verify
        mockMvc.perform(post("/vertex/delete/A"))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/vertices"));
    }

    @Test
    public void verifyThatListEdgesViewAndModelIsCorrect() throws Exception {
        //Set
        when(entityManagerService.getAllEdges()).thenReturn(edges);
        setUpFixture();
        //Verify
        mockMvc.perform(get("/edges"))
                .andExpect(model().attribute("edges", sameBeanAs(edges)))
                .andExpect(view().name("edges"));
    }

    @Test
    public void verifyThatShowEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Edge expectedEdge = new Edge(2, "2", "A", "C", 1.89f);
        long recordId = 2;
        when(entityManagerService.getEdgeById(recordId)).thenReturn(expectedEdge);
        //Verify
        mockMvc.perform(get("/edge/" + recordId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("edge", sameBeanAs(expectedEdge)))
                .andExpect(view().name("edgeshow"));
    }

    @Test
    public void verifyThatDeleteEdgeViewIsCorrect() throws Exception {
        //Set
        long recordId = 2;
        when(entityManagerService.deleteEdge(recordId)).thenReturn(true);
        //Verify
        mockMvc.perform(post("/edge/delete/" + recordId))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/edges"));
    }

    @Test
    public void verifyThatAddEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Edge expectedEdge = new Edge();
        ShortestPathModel sh = new ShortestPathModel();
        when(entityManagerService.getAllVertices()).thenReturn(vertices);
        //Verify
        mockMvc.perform(get("/edge/new"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("edge", sameBeanAs(expectedEdge)))
                .andExpect(model().attribute("edgeModel", sameBeanAs(sh)))
                .andExpect(model().attribute("routeList", sameBeanAs(vertices)))
                .andExpect(view().name("edgeadd"));
    }

    @Test
    public void verifyThatSaveEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Edge expectedEdge = new Edge(2, "2", "A", "C", 1.89f);
        long max = 1;
        when(entityManagerService.edgeExists(expectedEdge)).thenReturn(false);
        when(entityManagerService.getEdgeMaxRecordId()).thenReturn(max);
        when(entityManagerService.saveEdge(expectedEdge)).thenReturn(expectedEdge);

        //Test
        mockMvc.perform(post("/edge").param("recordId", "" + max).param("distance", "1.0").param("sourceVertex", "A").param("destinationVertex", "C"))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/edge/" + expectedEdge.getRecordId()));
    }

    @Test
    public void verifyThatSaveSameEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        long max = 1;
        when(entityManagerService.getEdgeMaxRecordId()).thenReturn(max);
        String message = "You cannot link a route to itself.";
        //Verify
        mockMvc.perform(post("/edge").param("recordId", "" + max).param("distance", "1.0").param("sourceVertex", "A").param("destinationVertex", "A"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
    }

    @Test
    public void verifyThatSaveExistingEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Edge expectedEdge = new Edge(2, "2", "A", "C", 1.89f);
        Vertex source = new Vertex("A", "Earth");
        long recordId = 1;
        when(entityManagerService.getEdgeMaxRecordId()).thenReturn(recordId);
        when(entityManagerService.edgeExists(any(Edge.class))).thenReturn(true);
        when(entityManagerService.getVertexById("A")).thenReturn(source);
        String message = "The route from Earth (A) to (C) exists already.";
        //Verify
        mockMvc.perform(post("/edge").param("recordId", "" + recordId).param("edgeId", "2").param("sourceVertex", "A").param("destinationVertex", "C").param("distance", "1.89"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("edge", sameBeanAs(expectedEdge)))
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
    }

    @Test
    public void verifyThatEditEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Edge expectedEdge = new Edge(1, "1", "A", "B", 0.44f);
        ShortestPathModel sh = new ShortestPathModel();
        when(entityManagerService.getAllVertices()).thenReturn(vertices);
        when(entityManagerService.getEdgeById(expectedEdge.getRecordId())).thenReturn(expectedEdge);
        sh.setSourceVertex(expectedEdge.getSource());
        sh.setDestinationVertex(expectedEdge.getDestination());
        //Verify
        mockMvc.perform(get("/edge/edit/" + expectedEdge.getRecordId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("edge", sameBeanAs(expectedEdge)))
                .andExpect(model().attribute("edgeModel", sameBeanAs(sh)))
                .andExpect(model().attribute("routeList", sameBeanAs(vertices)))
                .andExpect(view().name("edgeupdate"));
    }

    @Test
    public void verifyThatUpdateEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Edge expectedEdge = new Edge(2, "2", "A", "B", 1.89f);
        long recordId = 2;
        when(entityManagerService.edgeExists(expectedEdge)).thenReturn(false);
        when(entityManagerService.updateEdge(expectedEdge)).thenReturn(expectedEdge);

        //Test
        mockMvc.perform(post("/edgeupdate").param("recordId", "" + recordId).param("edgeId", "2").param("sourceVertex", "A").param("destinationVertex", "B").param("distance", "1.89"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("edge", sameBeanAs(expectedEdge)))
                .andExpect(view().name("redirect:/edge/" + expectedEdge.getRecordId()));
    }

    @Test
    public void verifyThatUpdateSameEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        long recordId = 1;
        String message = "You cannot link a route to itself.";
        //Verify
        mockMvc.perform(post("/edgeupdate").param("recordId", "" + recordId).param("edgeId", "2").param("sourceVertex", "A").param("destinationVertex", "A").param("distance", "1.89"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
    }

    @Test
    public void verifyThatUpdateExistingEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Edge expectedEdge = new Edge(2, "2", "A", "B", 1.89f);
        Vertex vertex = new Vertex("A", "Moon");
        long recordId = 2;
        when(entityManagerService.edgeExists(any(Edge.class))).thenReturn(true);
        when(entityManagerService.getVertexById("A")).thenReturn(vertex);
        String message = "The route from Moon (A) to (B) exists already.";
        //Verify
        mockMvc.perform(post("/edgeupdate").param("recordId", "" + recordId).param("edgeId", "2").param("sourceVertex", "A").param("destinationVertex", "B").param("distance", "1.89"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("edge", sameBeanAs(expectedEdge)))
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
    }

    //Traffic Tests

    @Test
    public void verifyThatListTrafficsViewAndModelIsCorrect() throws Exception {
        //Set
        when(entityManagerService.getAllTraffics()).thenReturn(traffics);
        setUpFixture();
        //Verify
        mockMvc.perform(get("/traffics"))
                .andExpect(model().attribute("traffics", sameBeanAs(traffics)))
                .andExpect(view().name("traffics"));
    }

    @Test
    public void verifyThatShowTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        Traffic expectedTraffic = new Traffic("1", "A", "B", 0.30f);
        when(entityManagerService.getTrafficById("1")).thenReturn(expectedTraffic);
        //Verify
        mockMvc.perform(get("/traffic/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("traffic", sameBeanAs(expectedTraffic)))
                .andExpect(view().name("trafficshow"));
    }

    @Test
    public void verifyThatDeleteTrafficViewIsCorrect() throws Exception {
        //Set
        when(entityManagerService.deleteTraffic("1")).thenReturn(true);
        //Verify
        mockMvc.perform(post("/traffic/delete/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/traffics"));
    }

    @Test
    public void verifyThatAddTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        Traffic expectedTraffic = new Traffic();
        ShortestPathModel sh = new ShortestPathModel();
        when(entityManagerService.getAllVertices()).thenReturn(vertices);
        //Verify
        mockMvc.perform(get("/traffic/new"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("traffic", sameBeanAs(expectedTraffic)))
                .andExpect(model().attribute("trafficModel", sameBeanAs(sh)))
                .andExpect(model().attribute("trafficList", sameBeanAs(vertices)))
                .andExpect(view().name("trafficadd"));
    }

    @Test
    public void verifyThatSaveTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        Traffic expectedTraffic = new Traffic("2", "A", "B", 1.0f);
        long max = 1;
        when(entityManagerService.trafficExists(expectedTraffic)).thenReturn(false);
        when(entityManagerService.getTrafficMaxRecordId()).thenReturn(max);
        when(entityManagerService.saveTraffic(expectedTraffic)).thenReturn(expectedTraffic);

        //Test
        mockMvc.perform(post("/traffic").param("routeId", "1").param("delay", "1.0").param("sourceVertex", "A").param("destinationVertex", "B"))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/traffic/" + expectedTraffic.getRouteId()));
    }

    @Test
    public void verifyThatSaveSameTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        long max = 1;
        when(entityManagerService.getTrafficMaxRecordId()).thenReturn(max);
        String message = "You cannot add traffic on the same route origin and destination.";
        //Verify
        mockMvc.perform(post("/traffic").param("routeId", "1").param("delay", "1.0").param("sourceVertex", "A").param("destinationVertex", "A"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
    }

    @Test
    public void verifyThatSaveExistingTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        Traffic expectedTraffic = new Traffic("2", "A", "B", 2.0f);
        Vertex source = new Vertex("A", "Earth");
        long recordId = 1;
        when(entityManagerService.getTrafficMaxRecordId()).thenReturn(recordId);
        when(entityManagerService.trafficExists(any(Traffic.class))).thenReturn(true);
        when(entityManagerService.getVertexById("A")).thenReturn(source);
        String message = "The traffic from Earth (A) to  (B) exists already.";
        //Verify
        mockMvc.perform(post("/traffic").param("routeId", "1").param("delay", "2.0").param("sourceVertex", "A").param("destinationVertex", "B"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("traffic", sameBeanAs(expectedTraffic)))
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
    }

    @Test
    public void verifyThatEditTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        Traffic expectedTraffic = new Traffic("2", "A", "B", 2.0f);
        ShortestPathModel sh = new ShortestPathModel();
        when(entityManagerService.getAllVertices()).thenReturn(vertices);
        when(entityManagerService.getTrafficById(expectedTraffic.getRouteId())).thenReturn(expectedTraffic);
        sh.setSourceVertex(expectedTraffic.getSource());
        sh.setDestinationVertex(expectedTraffic.getDestination());
        //Verify
        mockMvc.perform(get("/traffic/edit/" + expectedTraffic.getRouteId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("traffic", sameBeanAs(expectedTraffic)))
                .andExpect(model().attribute("trafficModel", sameBeanAs(sh)))
                .andExpect(model().attribute("trafficList", sameBeanAs(vertices)))
                .andExpect(view().name("trafficupdate"));
    }

    @Test
    public void verifyThatUpdateTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        Traffic expectedTraffic = new Traffic("2", "A", "B", 1.0f);
        when(entityManagerService.trafficExists(expectedTraffic)).thenReturn(false);
        when(entityManagerService.updateTraffic(expectedTraffic)).thenReturn(expectedTraffic);

        //Verify
        mockMvc.perform(post("/trafficupdate").param("routeId", "2").param("source", "A").param("destination", "C").param("sourceVertex", "A").param("destinationVertex", "B").param("delay", "1.0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("traffic", sameBeanAs(expectedTraffic)))
                .andExpect(view().name("redirect:/traffic/" + expectedTraffic.getRouteId()));
    }

    @Test
    public void verifyThatUpdateSameTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        String message = "You cannot add traffic on the same route origin and destination.";
        //Verify
        mockMvc.perform(post("/trafficupdate").param("routeId", "2").param("source", "A").param("destination", "C").param("sourceVertex", "A").param("destinationVertex", "A").param("delay", "1.0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
    }

    @Test
    public void verifyThatUpdateExistingTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        Traffic expectedTraffic = new Traffic("2", "A", "B", 1.0f);
        Vertex vertex = new Vertex("A", "Moon");
        when(entityManagerService.trafficExists(any(Traffic.class))).thenReturn(true);
        when(entityManagerService.getVertexById("A")).thenReturn(vertex);
        String message = "The traffic from Moon (A) to  (B) exists already.";
        //Verify
        mockMvc.perform(post("/trafficupdate").param("routeId", "2").param("source", "A").param("destination", "C").param("sourceVertex", "A").param("destinationVertex", "B").param("delay", "1.0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("traffic", sameBeanAs(expectedTraffic)))
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
    }

    @Test
    public void verifyThatShortestPathViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex expectedSource = vertices.get(0);
        when(entityManagerService.getAllVertices()).thenReturn(vertices);
        ShortestPathModel sh = new ShortestPathModel();
        sh.setVertexName(expectedSource.getName());
        //Verify
        mockMvc.perform(get("/shortest"))
                .andExpect(model().attribute("shortest", sameBeanAs(sh)))
                .andExpect(model().attribute("pathList", sameBeanAs(vertices)))
                .andExpect(view().name("shortest"));
    }

    @Test
    public void verifyThatShortestPathResultViewAndModelIsCorrect() throws Exception {
        //Set
        StringBuilder path = new StringBuilder();
        Vertex expectedSource = new Vertex("A", "Earth");
        Vertex step = new Vertex("B", "Moon");
        Vertex expectedDestination = new Vertex("E", "Mars");
        Graph graph = new Graph(vertices, edges, traffics);
        LinkedList<Vertex> pathList = new LinkedList<>();
        pathList.add(expectedSource);
        pathList.add(step);
        pathList.add(expectedDestination);
        when(entityManagerService.selectGraph()).thenReturn(graph);
        when(entityManagerService.getVertexByName("A")).thenReturn(expectedSource);
        when(entityManagerService.getVertexById("E")).thenReturn(expectedDestination);
        when(shortestPathService.getPath(expectedDestination)).thenReturn(pathList);

        path.append("Earth (A)\tMoon (B)\tMars (E)\t");
        ShortestPathModel pathModel = new ShortestPathModel();
        pathModel.setThePath(path.toString());
        pathModel.setSelectedVertexName(expectedDestination.getName());
        pathModel.setSelectedVertex("E");
        pathModel.setVertexId("A");
        pathModel.setVertexName("Earth");

        //Verify
        mockMvc.perform(post("/shortest").param("vertexId", "A").param("vertexName", "Earth").param("selectedVertex", "E").param("trafficAllowed", "false").param("undirectedGraph", "false"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("shortest", sameBeanAs(pathModel)))
                .andExpect(view().name("result"));
    }

    public void setUpFixture() {
        mockMvc = standaloneSetup(
                new RootController(entityManagerService, shortestPathService)
        )
                .setViewResolvers(getInternalResourceViewResolver())
                .build();
    }

    private InternalResourceViewResolver getInternalResourceViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setSuffix(".html");
        return viewResolver;
    }

}