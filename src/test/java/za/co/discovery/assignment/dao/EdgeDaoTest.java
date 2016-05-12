package za.co.discovery.assignment.dao;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;
import za.co.discovery.assignment.config.DatasourceBean;
import za.co.discovery.assignment.config.PersistenceBean;
import za.co.discovery.assignment.entity.Edge;
import za.co.discovery.assignment.entity.Traffic;
import za.co.discovery.assignment.entity.Vertex;

import java.util.Arrays;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;


/**
 * Created by Kapeshi.Kongolo on 2016/04/09.
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Edge.class, EdgeDao.class, Vertex.class, VertexDao.class, DatasourceBean.class, PersistenceBean.class},
        loader = AnnotationConfigContextLoader.class)

public class EdgeDaoTest {

    @Autowired
    private SessionFactory sessionFactory;
    private EdgeDao edgeDao;
    private int nextEdgeRecordId;

    @Before
    public void setUp() throws Exception {
        edgeDao = new EdgeDao(sessionFactory);
    }

    @Test
    public void verifyThatSaveEdgeIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        setUpFixtures();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);

        Edge edge = new Edge("2", vertex1, vertex2, 2f);
        Edge expectedEdge = new Edge("2", vertex1, vertex2, 2f);
        expectedEdge.setId(1L + nextEdgeRecordId);

        //Test
        edgeDao.save(edge);

        Edge persistedEdge = edgeDao.selectUnique(expectedEdge.getId());

        //Verify
        assertThat(persistedEdge, sameBeanAs(expectedEdge));
        assertEquals("Earth", edge.getSource().getName());
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSaveEdgeAlsoSaveTraffic() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        setUpFixtures();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);

        Edge edge = new Edge("2", vertex1, vertex2, 2f);

        Traffic traffic = new Traffic("2", 1f);
        edge.addTraffic(traffic);


        //Test
        edgeDao.save(edge);

        Edge expectedEdge = new Edge("2", vertex1, vertex2, 2f);
        expectedEdge.setId(1L + nextEdgeRecordId);
        Traffic expectedTraffic = new Traffic("2", 1f);
        expectedTraffic.setId(2L);
        expectedEdge.addTraffic(expectedTraffic);

        List<Traffic> expectedTraffics = singletonList(expectedTraffic);

        Criteria criteria = session.createCriteria(Traffic.class);
        List<Traffic> actualTraffics = (List<Traffic>) criteria.list();

        //Verify
        assertThat(actualTraffics, sameBeanAs(expectedTraffics));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSaveVertexAlsoSaveEdgeAndTraffic() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        setUpFixtures();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");

        Edge edge = new Edge("2", 2f);

        vertex1.addSourceEdges(edge);
        vertex2.addDestinationEdges(edge);

        Traffic traffic = new Traffic("2", 1f);
        edge.addTraffic(traffic);

        //Test
        session.save(vertex1);
        session.save(vertex2);

        Vertex expectedVertex1 = new Vertex("A", "Earth");
        Vertex expectedVertex2 = new Vertex("B", "Mars");

        Edge expectedEdge = new Edge("2", 2f);
        expectedEdge.setId(1L + nextEdgeRecordId);
        expectedVertex1.addSourceEdges(expectedEdge);
        expectedVertex2.addDestinationEdges(expectedEdge);

        Traffic expectedTraffic = new Traffic("2", 1f);
        expectedTraffic.setId(1L);
        expectedEdge.addTraffic(expectedTraffic);

        List<Edge> expectedEdges = singletonList(expectedEdge);
        List<Traffic> expectedTraffics = singletonList(expectedTraffic);

        Criteria criteriaTraffic = session.createCriteria(Traffic.class);
        List<Traffic> actualTraffics = (List<Traffic>) criteriaTraffic.list();

        Criteria criteriaEdge = session.createCriteria(Edge.class);
        List<Edge> actualEdges = (List<Edge>) criteriaEdge.list();

        //Verify
        assertThat(actualTraffics, sameBeanAs(expectedTraffics));
        assertThat(actualEdges, sameBeanAs(expectedEdges));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatUpdateEdgeIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        setUpFixtures();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);
        Edge edge = new Edge("20", vertex1, vertex2, 20f);
        session.save(edge);

        Vertex vertex3 = new Vertex("C", "Moon");
        session.save(vertex3);
        Edge expectedEdge = new Edge("20", vertex1, vertex3, 20f);
        expectedEdge.setId(nextEdgeRecordId + 1L);
        List<Edge> expectedEdges = singletonList(expectedEdge);

        //Test
        edgeDao.update(expectedEdge);
        List<Edge> persistedEdges = edgeDao.selectAllByRecordId(expectedEdge.getId());

        // Verify
        assertThat(persistedEdges, sameBeanAs(expectedEdges));
        assertEquals("Moon", edge.getDestination().getName());
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatDeleteEdgeIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        setUpFixtures();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex vertex3 = new Vertex("C", "Moon");
        Vertex vertex4 = new Vertex("D", "Jupiter");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);
        session.save(vertex4);

        Edge e1 = new Edge("10", vertex1, vertex2, 20.1f);
        Edge e2 = new Edge("12", vertex3, vertex4, 1.3f);
        session.save(e1);
        session.save(e2);


        Edge exp = new Edge("10", vertex1, vertex2, 20.1f);
        exp.setId(1L + nextEdgeRecordId);
        List<Edge> expectedEdges = singletonList(exp);

        //Test
        edgeDao.delete(e2);
        Criteria criteria = session.createCriteria(Edge.class);
        List<Edge> persistedEdges = (List<Edge>) criteria.list();

        // Verify
        assertThat(persistedEdges, sameBeanAs(expectedEdges));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectUniqueEdgeIsCorrect() {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);

        Edge edge = new Edge("5", vertex1, vertex2, 0.5f);
        Edge expectedEdge = new Edge("1", vertex1, vertex2, 20.1f);
        expectedEdge.setId(1L);
        session.save(edge);
        session.save(expectedEdge);

        //Test
        Edge persistedEdge = edgeDao.selectUnique(expectedEdge.getId());

        //Verify
        assertThat(persistedEdge, sameBeanAs(expectedEdge));
        assertEquals(expectedEdge, persistedEdge);
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectAllEdgesByRouteIdIsCorrect() {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        session.save(vertex1);
        session.save(vertex2);

        Edge e1 = new Edge("1", vertex1, vertex2, 2.4f);
        Edge e2 = new Edge("2", vertex2, vertex1, 1.3f);
        session.save(e1);
        session.save(e2);

        List<Edge> expectedEdges = singletonList(e1);

        //Test
        List<Edge> persistedEdge = edgeDao.selectAllByRouteId(e1.getRouteId());

        //Verify
        assertThat(persistedEdge, sameBeanAs(expectedEdges));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectAllEdgesIsCorrect() {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");


        Edge e1 = new Edge("4", 2.4f);
        Edge e2 = new Edge("9", 1.3f);

        vertex1.addSourceEdges(e1);
        vertex1.addDestinationEdges(e2);
        vertex2.addDestinationEdges(e1);
        vertex2.addSourceEdges(e2);


        session.save(vertex1);
        session.save(vertex2);

        List<Edge> expectedEdges = Arrays.asList(e2, e1);

        //Test
        List<Edge> persistedEdge = edgeDao.selectAll();

        //Verify
        assertThat(persistedEdge, sameBeanAs(expectedEdges));
        //Rollback for testing purpose

        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatEdgeExistsSelectionIsCorrect() {
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
        List<Edge> expectedEdges = singletonList(e1);

        //Test
        List<Edge> returnedEdges = edgeDao.edgeExists(edgeToCommit);
        //Verify
        assertThat(returnedEdges, sameBeanAs(expectedEdges));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    public void setUpFixtures() {
        nextEdgeRecordId = edgeDao.findNextId();
    }
}