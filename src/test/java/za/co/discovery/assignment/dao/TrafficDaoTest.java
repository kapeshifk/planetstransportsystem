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
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;


@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Traffic.class, TrafficDao.class, DatasourceBean.class, PersistenceBean.class},
        loader = AnnotationConfigContextLoader.class)
public class TrafficDaoTest {
    @Autowired
    private SessionFactory sessionFactory;
    private TrafficDao trafficDao;

    @Before
    public void setUp() throws Exception {
        trafficDao = new TrafficDao(sessionFactory);
    }

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
        trafficDao.save(traffic);
        Criteria criteria = session.createCriteria(Traffic.class);
        List<Traffic> persistedTraffics = (List<Traffic>) criteria.list();

        //Verify
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

        List<Traffic> expectedTraffic = new ArrayList<>();
        expectedTraffic.add(trafficToUpdate);

        //Test
        trafficDao.update(trafficToUpdate);
        Criteria criteria = session.createCriteria(Traffic.class);
        List<Traffic> persistedTraffics = (List<Traffic>) criteria.list();

        // Verify
        assertThat(persistedTraffics, sameBeanAs(expectedTraffic));
        assertThat(persistedTraffics.get(0).getRoute().getDestination().getName(), sameBeanAs("Venus"));

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

        //Test
        trafficDao.delete(traffic2.getId());
        Criteria criteria = session.createCriteria(Traffic.class);
        List<Traffic> persistedTraffics = (List<Traffic>) criteria.list();

        // Verify
        assertThat(persistedTraffics, sameBeanAs(expectedTraffics));

        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelectUniqueTrafficIsCorrect() throws Exception {
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
        Traffic expected = new Traffic("5", edge2, 4f);
        session.save(traffic);
        session.save(expected);

        //Test
        Traffic persisted = trafficDao.selectUniqueByRouteId(expected.getRouteId());

        //Verify
        assertThat(persisted, sameBeanAs(expected));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }

    @Test
    public void verifyThatSelecteAllTrafficsIsCorrect() throws Exception {
        //Set
        Session session = sessionFactory.getCurrentSession();
        Vertex vertex1 = new Vertex("A", "Earth");
        Vertex vertex2 = new Vertex("B", "Moon");
        Vertex vertex3 = new Vertex("C", "Congo");
        Vertex vertex4 = new Vertex("D", "Denzel");
        Vertex vertex5 = new Vertex("W", "Washington");
        session.save(vertex1);
        session.save(vertex2);
        session.save(vertex3);
        session.save(vertex4);
        session.save(vertex5);
        Edge edge = new Edge("20", vertex1, vertex2, 1f);
        Edge edge2 = new Edge("21", vertex1, vertex3, 2.0f);
        Edge edge3 = new Edge("23", vertex3, vertex4, 3.0f);
        Edge edge4 = new Edge("24", vertex1, vertex5, 4.0f);
        session.save(edge);
        session.save(edge2);
        session.save(edge3);
        session.save(edge4);

        Traffic traffic1 = new Traffic("1", edge, 4.1f);
        Traffic traffic2 = new Traffic("2", edge2, 1.2f);
        Traffic traffic3 = new Traffic("3", edge3, 11f);
        Traffic traffic4 = new Traffic("4", edge4, 4f);
        session.save(traffic1);
        session.save(traffic2);
        session.save(traffic3);
        session.save(traffic4);
        List<Traffic> expectedTraffics = new ArrayList<>();
        expectedTraffics.add(traffic1);
        expectedTraffics.add(traffic2);
        expectedTraffics.add(traffic3);
        expectedTraffics.add(traffic4);

        //Test
        List<Traffic> persistedTraffics = trafficDao.selectAll();

        //Verify
        assertThat(persistedTraffics, sameBeanAs(expectedTraffics));
        //Rollback for testing purpose
        session.getTransaction().rollback();
    }
}
