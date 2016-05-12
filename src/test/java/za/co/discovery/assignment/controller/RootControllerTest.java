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

import java.util.*;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

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
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Moon");
        Vertex vertexC = new Vertex("C", "Jupiter");
        Vertex vertexD = new Vertex("D", "Venus");
        Vertex vertexE = new Vertex("E", "Mars");

        vertices = new ArrayList<>();
        vertices.add(vertexA);
        vertices.add(vertexB);
        vertices.add(vertexC);
        vertices.add(vertexD);
        vertices.add(vertexE);

        Edge edge1 = new Edge("1", vertexA, vertexB, 0.44f);
        Edge edge2 = new Edge("2", vertexA, vertexC, 1.89f);
        Edge edge3 = new Edge("3", vertexA, vertexD, 0.10f);
        Edge edge4 = new Edge("4", vertexB, vertexC, 2.44f);
        Edge edge5 = new Edge("5", vertexB, vertexE, 3.45f);

        edges = new ArrayList<>();
        edges.add(edge1);
        edges.add(edge2);
        edges.add(edge3);
        edges.add(edge4);
        edges.add(edge5);

        Traffic traffic1 = new Traffic("1", edge1, 0.30f);
        Traffic traffic2 = new Traffic("2", edge2, 0.90f);
        Traffic traffic3 = new Traffic("3", edge3, 0.10f);
        Traffic traffic4 = new Traffic("4", edge4, 0.20f);
        Traffic traffic5 = new Traffic("5", edge5, 1.30f);

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
    public void verifyThatVertexViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex expectedVertex = new Vertex("A", "Earth");
        when(entityManagerService.getVertexById("A")).thenReturn(expectedVertex);
        //Verify
        mockMvc.perform(get("/vertex/A"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("vertex", sameBeanAs(expectedVertex)))
                .andExpect(view().name("vertex_view"));
    }

    @Test
    public void verifyThatAddVertexViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex expectedVertex = new Vertex();
        //Verify
        mockMvc.perform(get("/vertex/add"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("vertex", sameBeanAs(expectedVertex)))
                .andExpect(view().name("vertex_add"));
    }

    @Test
    public void verifyThatSaveVertexViewIsCorrect() throws Exception {
        //Set
        Vertex expectedVertex = new Vertex("A", "Earth");
        when(entityManagerService.vertexExist("A")).thenReturn(false);
        when(entityManagerService.saveVertex(expectedVertex)).thenReturn(expectedVertex);

        //Test
        mockMvc.perform(post("/save_vertex").param("id", "A").param("name", "Earth"))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/vertex/" + expectedVertex.getId()));

        //Verify
        ArgumentCaptor<Vertex> formObjectArgument = ArgumentCaptor.forClass(Vertex.class);
        verify(entityManagerService, times(1)).saveVertex(formObjectArgument.capture());

        Vertex formObject = formObjectArgument.getValue();
        assertThat(formObjectArgument.getValue(), is(sameBeanAs(expectedVertex)));

        assertThat(formObject.getId(), is("A"));
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
        mockMvc.perform(post("/save_vertex").param("id", "A").param("name", "Earth"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
    }

    @Test
    public void verifyThatEditVertexViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex expectedVertex = new Vertex("A", "Earth");
        when(entityManagerService.getVertexById("A")).thenReturn(expectedVertex);
        //Verify
        mockMvc.perform(get("/vertex/edit/A"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("vertex", sameBeanAs(expectedVertex)))
                .andExpect(view().name("vertex_update"));
    }

    @Test
    public void verifyThatUpdateVertexViewIsCorrect() throws Exception {
        //Set
        Vertex expectedVertex = new Vertex("A", "Earth");
        when(entityManagerService.updateVertex(expectedVertex)).thenReturn(expectedVertex);
        //Verify
        mockMvc.perform(post("/update_vertex").param("id", "A").param("name", "Earth"))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/vertex/" + expectedVertex.getId()));
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
    public void verifyThatEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexC = new Vertex("C", "Jupiter");
        Edge expectedEdge = new Edge("2", vertexA, vertexC, 1.89f);
        long recordId = 2;
        when(entityManagerService.getEdgeById(recordId)).thenReturn(expectedEdge);
        //Verify
        mockMvc.perform(get("/edge/" + recordId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("edge", sameBeanAs(expectedEdge)))
                .andExpect(view().name("edge_view"));
    }

    @Test
    public void verifyThatAddEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Edge expectedEdge = new Edge();
        ShortestPathModel sh = new ShortestPathModel();
        when(entityManagerService.getAllVertices()).thenReturn(vertices);
        //Verify
        mockMvc.perform(get("/edge/add"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("edge", sameBeanAs(expectedEdge)))
                .andExpect(model().attribute("edgeModel", sameBeanAs(sh)))
                .andExpect(model().attribute("routeList", sameBeanAs(vertices)))
                .andExpect(view().name("edge_add"));
    }

    @Test
    public void verifyThatSaveEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexC = new Vertex("C", "Jupiter");
        Edge expectedEdge = new Edge("2", vertexA, vertexC, 1.89f);
        expectedEdge.setId(1L);
        long record = 1;
        when(entityManagerService.edgeExists(expectedEdge)).thenReturn(false);
        when(entityManagerService.saveEdge(expectedEdge)).thenReturn(expectedEdge);

        //Test
        mockMvc.perform(post("/save_edge").param("id", "" + record).param("distance", "1.0").param("sourceVertex.id", "A").param("destinationVertex.id", "C"))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/edge/" + expectedEdge.getId()));
    }

    @Test
    public void verifyThatSaveSameEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        long id = 1;
        String message = "You cannot link a route to itself.";
        //Verify
        mockMvc.perform(post("/save_edge").param("id", "" + id).param("distance", "1.0").param("sourceVertex.id", "A").param("destinationVertex.id", "A"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
    }

    @Test
    public void verifyThatSaveExistingEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexC = new Vertex("C", "Jupiter");
        Edge expectedEdge = new Edge("2", vertexA, vertexC, 1.89f);
        expectedEdge.setId(1L);
        Vertex source = new Vertex("A", "Earth");
        long recordId = 1;
        when(entityManagerService.edgeExists(any(Edge.class))).thenReturn(true);
        when(entityManagerService.getVertexById("A")).thenReturn(source);
        String message = "The route from Earth (A) to Jupiter(C) exists already.";
        //Verify
        mockMvc.perform(post("/save_edge").param("id", "" + recordId).param("routeId", "2").param("sourceVertex.id", "A").param("destinationVertex.id", "C").param("sourceVertex.name", "Earth").param("destinationVertex.name", "Jupiter").param("distance", "1.89"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("edge", sameBeanAs(expectedEdge)))
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
    }

    @Test
    public void verifyThatEditEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Moon");
        Edge expectedEdge = new Edge("1", vertexA, vertexB, 0.44f);
        expectedEdge.setId(1L);
        ShortestPathModel sh = new ShortestPathModel();
        when(entityManagerService.getAllVertices()).thenReturn(vertices);
        when(entityManagerService.getEdgeById(expectedEdge.getId())).thenReturn(expectedEdge);
        sh.setSourceVertex(expectedEdge.getSource());
        sh.setDestinationVertex(expectedEdge.getDestination());
        //Verify
        mockMvc.perform(get("/edge/edit/" + expectedEdge.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("edge", sameBeanAs(expectedEdge)))
                .andExpect(model().attribute("edgeModel", sameBeanAs(sh)))
                .andExpect(model().attribute("routeList", sameBeanAs(vertices)))
                .andExpect(view().name("edge_update"));
    }

    @Test
    public void verifyThatUpdateEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Moon");
        Edge expectedEdge = new Edge("A_B", vertexA, vertexB, 1.82f);
        expectedEdge.setId(2L);
        long recordId = 2;
        when(entityManagerService.edgeExists(expectedEdge)).thenReturn(false);
        when(entityManagerService.updateEdge(expectedEdge)).thenReturn(expectedEdge);

        //Test
        mockMvc.perform(post("/update_edge").param("id", "" + recordId).param("routeId", "A_B").param("sourceVertex.id", "A").param("destinationVertex.id", "B").param("sourceVertex.name", "Earth").param("destinationVertex.name", "Moon").param("distance", "1.82"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("edge", sameBeanAs(expectedEdge)))
                .andExpect(view().name("redirect:/edge/" + expectedEdge.getId()));
    }

    @Test
    public void verifyThatUpdateSameEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        long recordId = 1;
        String message = "You cannot link a route to itself.";
        //Verify
        mockMvc.perform(post("/update_edge").param("id", "" + recordId).param("route", "2").param("sourceVertex.id", "A").param("destinationVertex.id", "A").param("distance", "1.89"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
    }

    @Test
    public void verifyThatUpdateExistingEdgeViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Moon");
        Edge expectedEdge = new Edge("2", vertexA, vertexB, 1.89f);
        expectedEdge.setId(2L);
        Vertex vertex = new Vertex("A", "Moon");
        long recordId = 2;
        when(entityManagerService.edgeExists(any(Edge.class))).thenReturn(true);
        when(entityManagerService.getVertexById("A")).thenReturn(vertex);
        String message = "The route from Earth (A) to Moon(B) exists already.";
        //Verify
        mockMvc.perform(post("/update_edge").param("id", "" + recordId).param("routeId", "2").param("sourceVertex.id", "A").param("destinationVertex.id", "B").param("sourceVertex.name", "Earth").param("destinationVertex.name", "Moon").param("distance", "1.89"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("edge", sameBeanAs(expectedEdge)))
                .andExpect(model().attribute("validationMessage", sameBeanAs(message)))
                .andExpect(view().name("validation"));
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
    public void verifyThatViewTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Moon");
        Edge edge1 = new Edge("1", vertexA, vertexB, 0.44f);
        Traffic expectedTraffic = new Traffic("1", edge1, 0.30f);
        when(entityManagerService.getTrafficById(1l)).thenReturn(expectedTraffic);
        //Verify
        mockMvc.perform(get("/traffic/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("traffic", sameBeanAs(expectedTraffic)))
                .andExpect(view().name("traffic_view"));
    }

    @Test
    public void verifyThatAddTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        Traffic expectedTraffic = new Traffic();
        ShortestPathModel sh = new ShortestPathModel();
        when(entityManagerService.getAllUnusedEdges()).thenReturn(edges);
        //Verify
        mockMvc.perform(get("/traffic/add"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("traffic", sameBeanAs(expectedTraffic)))
                .andExpect(model().attribute("trafficModel", sameBeanAs(sh)))
                .andExpect(model().attribute("trafficList", sameBeanAs(edges)))
                .andExpect(view().name("traffic_add"));
    }

    @Test
    public void verifyThatSaveTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Moon");
        Edge edge1 = new Edge("1", vertexA, vertexB, 0.44f);
        edge1.setId(1L);
        Traffic expectedTraffic = new Traffic("2", edge1, 1.0f);
        expectedTraffic.setId(1L);
        when(entityManagerService.saveTraffic(expectedTraffic)).thenReturn(expectedTraffic);

        //Test
        mockMvc.perform(post("/save_traffic").param("id", "1").param("delay", "1.0").param("selectedEdge.id", "1").param("selectedEdge.source.id", "A").param("selectedEdge.destination.id", "B"))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/traffic/" + expectedTraffic.getId()));
    }

    @Test
    public void verifyThatEditTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Moon");
        Edge edge1 = new Edge("1", vertexA, vertexB, 0.44f);
        Traffic expectedTraffic = new Traffic("2", edge1, 2.0f);
        expectedTraffic.setId(1L);
        ShortestPathModel sh = new ShortestPathModel();
        when(entityManagerService.getAllEdges()).thenReturn(edges);
        when(entityManagerService.getTrafficById(expectedTraffic.getId())).thenReturn(expectedTraffic);
        sh.setSelectedEdge(expectedTraffic.getRoute());
        //Verify
        mockMvc.perform(get("/traffic/edit/" + expectedTraffic.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("traffic", sameBeanAs(expectedTraffic)))
                .andExpect(model().attribute("trafficModel", sameBeanAs(sh)))
                .andExpect(model().attribute("trafficList", sameBeanAs(edges)))
                .andExpect(view().name("traffic_update"));
    }

    @Test
    public void verifyThatUpdateTrafficViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Moon");
        Edge edge1 = new Edge("1", vertexA, vertexB, 0.44f);
        edge1.setId(2L);
        Traffic expectedTraffic = new Traffic("2", edge1, 1.0f);
        expectedTraffic.setId(1L);
        when(entityManagerService.updateTraffic(expectedTraffic)).thenReturn(expectedTraffic);

        //Verify
        mockMvc.perform(post("/update_traffic").param("id", "1").param("routeId", "2").param("route.id", "2").param("route.routeId", "1").param("route.source.id", "A").param("route.source.name", "Earth").param("route.destination.id", "B").param("route.destination.name", "Moon").param("route.distance", "0.44").param("selectedEdge.id", "2").param("delay", "1.0"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("traffic", sameBeanAs(expectedTraffic)))
                .andExpect(view().name("redirect:/traffic/" + expectedTraffic.getId()));
    }

    @Test
    public void verifyThatDeleteTrafficViewIsCorrect() throws Exception {
        //Set
        when(entityManagerService.deleteTraffic(1l)).thenReturn(true);
        //Verify
        mockMvc.perform(post("/traffic/delete/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/traffics"));
    }

    @Test
    public void verifyThatShortestPathViewAndModelIsCorrect() throws Exception {
        //Set
        Vertex expectedSource = vertices.get(0);
        when(entityManagerService.getAllVertices()).thenReturn(vertices);
        ShortestPathModel sh = new ShortestPathModel();
        sh.setVertexName(expectedSource.getName());
        sh.setVertexId(expectedSource.getId());
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

        Map<Vertex, Vertex> expectedPreviousPaths = new HashMap<>();
        expectedPreviousPaths.put(expectedDestination, step);
        expectedPreviousPaths.put(step, expectedSource);

        when(entityManagerService.selectGraph()).thenReturn(graph);
        when(entityManagerService.getVertexById("A")).thenReturn(expectedSource);
        when(entityManagerService.getVertexById("E")).thenReturn(expectedDestination);
        when(shortestPathService.run(graph, expectedSource)).thenReturn(expectedPreviousPaths);
        when(shortestPathService.getPath(expectedPreviousPaths, expectedDestination)).thenReturn(pathList);

        path.append("Earth (A)\tMoon (B)\tMars (E)\t");
        ShortestPathModel pathModel = new ShortestPathModel();
        pathModel.setThePath(path.toString());
        pathModel.setSelectedVertexName(expectedDestination.getName());
        pathModel.setSelectedVertex(expectedDestination);
        pathModel.setVertexId("A");
        pathModel.setVertexName("Earth");

        //Verify
        mockMvc.perform(post("/shortest").param("vertexId", "A").param("vertexName", "Earth").param("selectedVertex.id", "E").param("selectedVertex.name", "Mars").param("trafficAllowed", "false").param("undirectedGraph", "false"))
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
