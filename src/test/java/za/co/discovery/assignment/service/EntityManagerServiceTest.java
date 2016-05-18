package za.co.discovery.assignment.service;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import za.co.discovery.assignment.dao.EdgeDao;
import za.co.discovery.assignment.dao.TrafficDao;
import za.co.discovery.assignment.dao.VertexDao;
import za.co.discovery.assignment.entity.Edge;
import za.co.discovery.assignment.entity.Traffic;
import za.co.discovery.assignment.entity.Vertex;
import za.co.discovery.assignment.helper.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/persistence-config.xml", "/spring/services-config.xml"})
public class EntityManagerServiceTest {
    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private XLSXHandler xlsxHandler;
    private EdgeDao edgeDao;
    private VertexDao vertexDao;
    private TrafficDao trafficDao;
    private EntityManagerService entityManagerService;
    private int nextEdgeRecordId;
    private int nextTrafficRecordId;

    @Before
    public void setUp() throws Exception {
        edgeDao = new EdgeDao(sessionFactory);
        trafficDao = new TrafficDao(sessionFactory);
        vertexDao = new VertexDao(sessionFactory);
        entityManagerService = new EntityManagerService(vertexDao, edgeDao, trafficDao, xlsxHandler);
    }

    public void setEdgeRecord() {
        nextEdgeRecordId = edgeDao.findNextId();
    }

    public void setTrafficRecord() {
        nextTrafficRecordId = trafficDao.findNextId();
    }

    @Test
    public void verifyThatReadExcelAndPersistToGraphIsCorrect() throws Exception {
        Session session = sessionFactory.getCurrentSession();
        setEdgeRecord();
        setTrafficRecord();
        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexB = new Vertex("B", "Moon");
        Vertex vertexC = new Vertex("C", "Jupiter");
        Vertex vertexD = new Vertex("D", "Venus");
        Vertex vertexE = new Vertex("E", "Mars");

        Edge edge1 = new Edge("A_B", 0.44f);
        edge1.setId(nextEdgeRecordId + 1L);
        Edge edge2 = new Edge("A_C", 1.89f);
        edge2.setId(nextEdgeRecordId + 2L);
        Edge edge3 = new Edge("A_D", 0.10f);
        edge3.setId(nextEdgeRecordId + 3L);
        Edge edge4 = new Edge("B_E", 2.44f);
        edge4.setId(nextEdgeRecordId + 4L);
        Edge edge5 = new Edge("C_E", 3.45f);
        edge5.setId(nextEdgeRecordId + 5L);

        Traffic traffic1 = new Traffic("A_B", 0.30f);
        traffic1.setId(nextTrafficRecordId + 1L);
        Traffic traffic2 = new Traffic("A_C", 0.90f);
        traffic2.setId(nextTrafficRecordId + 2L);
        Traffic traffic3 = new Traffic("A_D", 0.10f);
        traffic3.setId(nextTrafficRecordId + 3L);
        Traffic traffic4 = new Traffic("B_E", 0.20f);
        traffic4.setId(nextTrafficRecordId + 4L);
        Traffic traffic5 = new Traffic("C_E", 1.30f);
        traffic5.setId(nextTrafficRecordId + 5L);

        edge1.addTraffic(traffic1);
        edge2.addTraffic(traffic2);
        edge3.addTraffic(traffic3);
        edge4.addTraffic(traffic4);
        edge5.addTraffic(traffic5);

        vertexA.addSourceEdges(edge1);
        vertexB.addDestinationEdges(edge1);
        vertexA.addSourceEdges(edge2);
        vertexC.addDestinationEdges(edge2);
        vertexA.addSourceEdges(edge3);
        vertexD.addDestinationEdges(edge3);
        vertexB.addSourceEdges(edge4);
        vertexE.addDestinationEdges(edge4);
        vertexC.addSourceEdges(edge5);
        vertexE.addDestinationEdges(edge5);

        List<Vertex> vertices = Arrays.asList(vertexA, vertexB, vertexC, vertexD, vertexE);
        List<Edge> edges = Arrays.asList(edge1, edge2, edge3, edge4, edge5);
        List<Traffic> traffics = Arrays.asList(traffic1, traffic2, traffic3, traffic4, traffic5);
        entityManagerService.readExcelFileAndImportIntoDatabase();
        Graph graph = entityManagerService.selectGraph();

        List<Edge> readEdges = graph.getEdges();
        List<Vertex> readVertices = graph.getVertexes();
        List<Traffic> readTraffics = graph.getTraffics();

        assertThat(vertices, sameBeanAs(readVertices));
        assertThat(edges, sameBeanAs(readEdges));
        assertThat(traffics, sameBeanAs(readTraffics));

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSaveVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex = new Vertex("A", "Earth");
        List<Vertex> expectedVertexes = new ArrayList<>();
        expectedVertexes.add(vertex);
        //Test
        Vertex returned = entityManagerService.saveVertex(vertex);
        Criteria criteria = session.createCriteria(Vertex.class);
        List<Vertex> persistedVertexes = (List<Vertex>) criteria.list();

        //Verify
        assertThat(vertex, sameBeanAs(returned));
        assertThat(persistedVertexes, sameBeanAs(expectedVertexes));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatUpdateVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex = new Vertex("A", "Earth");
        session.save(vertex);

        Vertex vertexToUpdate = new Vertex("A", "Jupiter");
        List<Vertex> expectedVertexes = new ArrayList<>();
        expectedVertexes.add(vertexToUpdate);

        Vertex persistedVertex = entityManagerService.updateVertex(vertexToUpdate);

        List<Vertex> persistedVertexes = new ArrayList<>();
        persistedVertexes.add(persistedVertex);

        assertThat(expectedVertexes, sameBeanAs(persistedVertexes));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatDeleteVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex v1 = new Vertex("A", "Mars");
        Vertex v2 = new Vertex("C", "Terre");
        List<Vertex> expectedVertexes = new ArrayList<>();
        expectedVertexes.add(v1);
        session.save(v1);
        session.save(v2);
        boolean expected = true;

        //Test
        boolean returned = entityManagerService.deleteVertex(v2.getId());

        // Verify
        assertThat(expected, sameBeanAs(returned));

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetUniqueByNameVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex expected = new Vertex("C", "Moon");
        session.save(vertex1);
        session.save(vertex2);
        session.save(expected);

        //Test
        Vertex persistedVertex = entityManagerService.getVertexByName(expected.getName());

        //Verify
        assertThat(persistedVertex, sameBeanAs(expected));
        assertThat(persistedVertex.getName(), sameBeanAs("Moon"));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetUniqueByIdVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex expected = new Vertex("C", "Moon");
        session.save(vertex1);
        session.save(expected);

        //Test
        Vertex persistedVertex = entityManagerService.getVertexById(expected.getId());

        //Verify
        assertThat(persistedVertex, sameBeanAs(expected));
        assertThat(persistedVertex.getId(), sameBeanAs("C"));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetAllVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex v1 = new Vertex("A", "Jupiter");
        Vertex v2 = new Vertex("F", "Pluto");
        session.save(v1);
        session.save(v2);
        List<Vertex> expectedVertexes = new ArrayList<>();
        expectedVertexes.add(v1);
        expectedVertexes.add(v2);

        //Test
        List<Vertex> persistedVertexes = entityManagerService.getAllVertices();

        //Verify
        assertThat(persistedVertexes, sameBeanAs(expectedVertexes));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatVertexExistsIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        session.save(vertex1);

        boolean expected = true;

        //Test
        boolean returned = entityManagerService.vertexExist(vertex1.getId());

        //Verify
        assertThat(returned, sameBeanAs(expected));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    //Edges

    @Test
    public void verifyThatSaveEdgeIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);
        Edge edge = new Edge("2", vertex1, vertex2, 2f);
        List<Edge> expectedEdges = new ArrayList<>();
        expectedEdges.add(edge);
        //Test
        Edge returned = entityManagerService.saveEdge(edge);
        Criteria criteria = session.createCriteria(Edge.class);
        List<Edge> persistedEdges = (List<Edge>) criteria.list();

        //Verify
        assertThat(edge, sameBeanAs(returned));
        assertThat(persistedEdges, sameBeanAs(expectedEdges));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatUpdateEdgeIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);
        Edge edge = new Edge("20", vertex1, vertex2, 20f);
        session.save(edge);

        Vertex vertex3 = new Vertex("C", "Moon");
        session.save(vertex3);
        Edge edgeToUpdate = new Edge("20", vertex1, vertex3, 20f);
        List<Edge> expectedEdges = new ArrayList<>();
        expectedEdges.add(edgeToUpdate);

        Edge persistedEdge = entityManagerService.updateEdge(edgeToUpdate);

        List<Edge> persistedEdges = new ArrayList<>();
        persistedEdges.add(persistedEdge);

        assertThat(expectedEdges, sameBeanAs(persistedEdges));
        assertThat(persistedEdge.getDestination(), sameBeanAs(vertex3));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatDeleteEdgeIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex vertex3 = new Vertex("C", "Moon");
        Vertex vertex4 = new Vertex("D", "Jupiter");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);
        session.save(vertex4);

        Edge edge1 = new Edge("10", vertex1, vertex2, 20.1f);
        Edge edge2 = new Edge("12", vertex3, vertex4, 1.3f);
        session.save(edge1);
        session.save(edge2);
        List<Edge> expectedEdges = new ArrayList<>();
        expectedEdges.add(edge1);

        boolean expected = true;

        //Test
        boolean returned = entityManagerService.deleteEdge(edge2.getId());

        // Verify
        assertThat(expected, sameBeanAs(returned));

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetEdgeByIdIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);

        Edge edge1 = new Edge("5", vertex1, vertex2, 0.5f);
        Edge expected = new Edge("1", vertex1, vertex2, 20.1f);
        session.save(edge1);
        session.save(expected);

        //Test
        Edge persistedEdge = entityManagerService.getEdgeById(expected.getId());

        //Verify
        assertThat(persistedEdge, sameBeanAs(expected));
        assertThat(persistedEdge.getDestination(), sameBeanAs(vertex2));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetAllEdgesIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);

        Edge edge1 = new Edge("1", vertex1, vertex2, 2.4f);
        Edge edge2 = new Edge("2", vertex2, vertex1, 1.3f);
        session.save(edge1);
        session.save(edge2);
        List<Edge> expectedEdges = new ArrayList<>();
        expectedEdges.add(edge1);
        expectedEdges.add(edge2);

        //Test
        List<Edge> persistedEdges = entityManagerService.getAllEdges();

        //Verify
        assertThat(persistedEdges, sameBeanAs(expectedEdges));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatEdgeExistsIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex vertex3 = new Vertex("C", "Moon");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);

        Edge e1 = new Edge("1", vertex1, vertex2, 2.4f);
        Edge e2 = new Edge("2", vertex1, vertex3, 100.3f);
        session.save(e1);
        session.save(e2);

        Edge edgeToCommit = new Edge("3", vertex1, vertex2, 0.3f);
        edgeToCommit.setId(3L);

        boolean expected = true;

        //Test
        boolean returned = entityManagerService.edgeExists(edgeToCommit);

        //Verify
        assertThat(returned, sameBeanAs(expected));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    //Traffcs

    @Test
    public void verifyThatSaveTrafficIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);
        Edge edge = new Edge("20", vertex1, vertex2, 20f);
        session.save(edge);

        Traffic traffic = new Traffic("1", edge, 4f);
        List<Traffic> expectedTraffics = new ArrayList<>();
        expectedTraffics.add(traffic);
        //Test
        Traffic returned = entityManagerService.saveTraffic(traffic);
        Criteria criteria = session.createCriteria(Traffic.class);
        List<Traffic> persistedTraffics = (List<Traffic>) criteria.list();

        //Verify
        assertThat(traffic, sameBeanAs(returned));
        assertThat(persistedTraffics, sameBeanAs(expectedTraffics));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatUpdateTrafficIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex vertex3 = new Vertex("C", "Venus");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);
        Edge edge = new Edge("20", vertex1, vertex2, 20f);
        Edge edge2 = new Edge("21", vertex1, vertex3, 2.0f);
        session.save(edge);
        session.save(edge2);

        Traffic traffic = new Traffic("1", edge, 4f);
        session.save(traffic);
        Long id = traffic.getId();

        Traffic trafficToUpdate = new Traffic("1", edge2, 1.2f);
        trafficToUpdate.setId(id);
        List<Traffic> expectedTraffics = new ArrayList<>();
        expectedTraffics.add(trafficToUpdate);

        Traffic persistedTraffic = entityManagerService.updateTraffic(trafficToUpdate);

        List<Traffic> persistedTraffics = new ArrayList<>();
        persistedTraffics.add(persistedTraffic);

        assertThat(expectedTraffics, sameBeanAs(persistedTraffics));
        assertThat(persistedTraffic.getRoute(), sameBeanAs(edge2));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatDeleteTrafficIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex vertex3 = new Vertex("C", "Venus");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);
        Edge edge = new Edge("20", vertex1, vertex2, 20f);
        Edge edge2 = new Edge("21", vertex1, vertex3, 2.0f);
        session.save(edge);
        session.save(edge2);

        Traffic traffic1 = new Traffic("1", edge, 4f);
        Traffic traffic2 = new Traffic("2", edge2, 2f);
        List<Traffic> expectedTraffics = new ArrayList<>();
        expectedTraffics.add(traffic1);
        session.save(traffic1);
        session.save(traffic2);
        boolean expected = true;

        //Test
        boolean returned = entityManagerService.deleteTraffic(traffic2.getId());

        // Verify
        assertThat(expected, sameBeanAs(returned));

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetTrafficByIdIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("F", "Moon");
        Vertex vertex3 = new Vertex("Z", "Congo");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);
        Edge edge = new Edge("20", vertex1, vertex2, 20f);
        Edge edge2 = new Edge("21", vertex1, vertex3, 2.0f);
        session.save(edge);
        session.save(edge2);

        Traffic traffic = new Traffic("100", edge, 4f);
        Traffic expected = new Traffic("5", edge2, 1.89f);
        session.save(traffic);
        session.save(expected);

        //Test
        Traffic persistedTraffic = entityManagerService.getTrafficById(expected.getId());

        //Verify
        assertThat(persistedTraffic, sameBeanAs(expected));
        assertThat(persistedTraffic.getDelay(), sameBeanAs(1.89f));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatGetAllTrafficsIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Moon");
        Vertex vertex3 = new Vertex("C", "Congo");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);

        Edge edge = new Edge("20", vertex1, vertex2, 1f);
        Edge edge2 = new Edge("21", vertex1, vertex3, 2.0f);
        session.save(edge);
        session.save(edge2);

        Traffic traffic1 = new Traffic("1", edge, 4.1f);
        Traffic traffic2 = new Traffic("2", edge2, 1.2f);
        session.save(traffic1);
        session.save(traffic2);
        List<Traffic> expectedTraffics = new ArrayList<>();
        expectedTraffics.add(traffic1);
        expectedTraffics.add(traffic2);

        //Test
        List<Traffic> persistedTraffics = entityManagerService.getAllTraffics();

        //Verify
        assertThat(persistedTraffics, sameBeanAs(expectedTraffics));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }
}
