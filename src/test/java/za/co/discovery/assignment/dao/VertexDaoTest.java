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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertEquals;


/**
 * Created by Kapeshi.Kongolo on 2016/04/11.
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Edge.class, EdgeDao.class, Vertex.class, VertexDao.class, DatasourceBean.class, PersistenceBean.class},
        loader = AnnotationConfigContextLoader.class)
public class VertexDaoTest {
    @Autowired
    private SessionFactory sessionFactory;
    private VertexDao vertexDao;

    @Before
    public void setUp() throws Exception {
        vertexDao = new VertexDao(sessionFactory);
    }

    @Test
    public void verifyThatSaveVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex = new Vertex("A", "Earth");
        List<Vertex> expectedVertexes = new ArrayList<>();
        expectedVertexes.add(vertex);

        //Test
        vertexDao.save(vertex);
        Criteria criteria = session.createCriteria(Vertex.class);
        List<Vertex> persistedVertexes = (List<Vertex>) criteria.list();

        //Verify
        assertThat(persistedVertexes, sameBeanAs(expectedVertexes));
        assertEquals("Earth", persistedVertexes.get(0).getName());
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

        //Test
        vertexDao.update(vertexToUpdate);
        Criteria criteria = session.createCriteria(Vertex.class);
        List<Vertex> persistedVertexes = (List<Vertex>) criteria.list();

        // Verify
        assertThat(persistedVertexes, sameBeanAs(expectedVertexes));

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatUpdateVertexAlsoUpdateEdges() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex = new Vertex("A", "Earth");
        session.save(vertex);

        Vertex vertexToUpdate = new Vertex("A", "Jupiter");

        Vertex vertex2 = new Vertex("B", "Moon");
        session.save(vertex2);

        List<Vertex> expectedVertexes = new ArrayList<>();
        expectedVertexes.add(vertexToUpdate);
        expectedVertexes.add(vertex2);

        Edge edge = new Edge("2", 20f);

        vertex.addSourceEdges(edge);
        vertex2.addDestinationEdges(edge);

        //Test
        vertexDao.update(vertexToUpdate);
        Criteria criteria = session.createCriteria(Vertex.class);
        List<Vertex> persistedVertexes = (List<Vertex>) criteria.list();

        // Verify
        assertThat(persistedVertexes, sameBeanAs(expectedVertexes));
        assertEquals("Jupiter", edge.getSource().getName());

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatDeleteVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex v1 = new Vertex("A", "Mars");
        Vertex v2 = new Vertex("C", "Terre");
        List<Vertex> expectedVertexes = singletonList(v1);
        session.save(v1);
        session.save(v2);

        //Test
        vertexDao.delete(v2.getId());
        Criteria criteria = session.createCriteria(Vertex.class);
        List<Vertex> persistedVertexes = (List<Vertex>) criteria.list();

        // Verify
        assertThat(persistedVertexes, sameBeanAs(expectedVertexes));

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatDeleteVertexAlsoDeleteEdges() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex v1 = new Vertex("A", "Earth");
        Vertex v2 = new Vertex("B", "Moon");
        Vertex v3 = new Vertex("C", "Jupiter");
        Vertex v4 = new Vertex("D", "Venus");

        Edge edge1 = new Edge("1", 20f);
        Edge edge2 = new Edge("2", 11f);
        Edge edge3 = new Edge("3", 11f);

        v1.addSourceEdges(edge1);
        v2.addDestinationEdges(edge1);
        v1.addSourceEdges(edge2);
        v3.addDestinationEdges(edge2);
        v1.addSourceEdges(edge3);
        v4.addDestinationEdges(edge3);

        Traffic traffic1 = new Traffic("1", 1f);
        Traffic traffic2 = new Traffic("2", 1f);
        Traffic traffic3 = new Traffic("3", 1f);

        edge1.addTraffic(traffic1);
        edge2.addTraffic(traffic2);
        edge3.addTraffic(traffic3);

        session.save(v1);
        session.save(v2);
        session.save(v3);
        session.save(v4);

        List<Vertex> expectedVertexes = Arrays.asList(v2, v3, v4);

        //Test
        v1.removeSourceEdges(edge1);
        v2.removeDestinationEdges(edge1);
        v1.removeSourceEdges(edge2);
        v3.removeDestinationEdges(edge2);
        v1.removeSourceEdges(edge3);
        v4.removeDestinationEdges(edge3);
        vertexDao.delete(v1.getId());

        Criteria allVertices = session.createCriteria(Vertex.class);
        List<Vertex> persistedVertexes = (List<Vertex>) allVertices.list();

        Criteria allEdges = session.createCriteria(Edge.class);
        List<Edge> edges = (List<Edge>) allEdges.list();

        // Verify
        assertThat(persistedVertexes, sameBeanAs(expectedVertexes));
        assertThat(edges, is(empty()));

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectUniqueVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex v1 = new Vertex("A", "Mars");
        Vertex expected = new Vertex("C", "Terre");
        session.save(v1);
        session.save(expected);

        //Test
        Vertex actualVertex = vertexDao.selectUnique(expected.getId());

        //Verify
        assertThat(actualVertex, sameBeanAs(expected));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectUniqueByNameVertexIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Mars");
        Vertex expected = new Vertex("C", "Moon");
        session.save(vertex1);
        session.save(vertex2);
        session.save(expected);

        //Test
        Vertex persistedVertex = vertexDao.selectUniqueByName(expected.getName());

        //Verify
        assertThat(persistedVertex, sameBeanAs(expected));
        assertThat(persistedVertex.getName(), sameBeanAs("Moon"));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectAllVertexIsCorrect() throws Exception {
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
        List<Vertex> persistedVertexes = vertexDao.selectAll();

        //Verify
        assertThat(persistedVertexes, sameBeanAs(expectedVertexes));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

}