package za.co.discovery.assignment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import za.co.discovery.assignment.config.DatasourceBean;
import za.co.discovery.assignment.config.PersistenceBean;
import za.co.discovery.assignment.entity.Edge;
import za.co.discovery.assignment.entity.Traffic;
import za.co.discovery.assignment.entity.Vertex;
import za.co.discovery.assignment.helper.Graph;
import za.co.discovery.assignment.service.EntityManagerService;
import za.co.discovery.assignment.service.ShortestPathService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ShortestPathService.class, DatasourceBean.class, PersistenceBean.class},
        loader = AnnotationConfigContextLoader.class)
public class ShortestPathRepositoryTest {
    @Autowired
    @Qualifier("transactionManager")
    protected PlatformTransactionManager platformTransactionManager;

    @Autowired
    private ShortestPathService shortestPathService;

    @Test
    public void verifyThatDataInitializeAndGiveCorrectPath() throws Exception {

        // SetUp Fixture
        EntityManagerService entityManagerService = mock(EntityManagerService.class);

        Vertex vertexA = new Vertex("A", "Earth");
        Vertex vertexF = new Vertex("F", "Pluto");
        List<Vertex> vertices = new ArrayList<>();
        vertices.add(vertexA);
        vertices.add(vertexF);
        Edge edge1 = new Edge("30", vertexA, vertexF, 0.17f);
        List<Edge> edges = new ArrayList<>();
        edges.add(edge1);
        Traffic traffic = new Traffic("1", edge1, 4f);
        List<Traffic> traffics = new ArrayList<>();
        traffics.add(traffic);

        StringBuilder path = new StringBuilder();
        Vertex expectedSource = vertices.get(0);
        Vertex expectedDestination = vertices.get(1);
        Graph graph = new Graph(vertices, edges, traffics);
        LinkedList<Vertex> pathList = new LinkedList<>();
        pathList.add(expectedSource);
        pathList.add(expectedDestination);
        when(entityManagerService.selectGraph()).thenReturn(graph);
        when(entityManagerService.getVertexByName(expectedDestination.getName())).thenReturn(expectedDestination);
        when(entityManagerService.getVertexById(expectedDestination.getId())).thenReturn(expectedDestination);

        path.append("Earth (A)\tPluto (F)\t");
        ShortestPathRepository pathRepository = new ShortestPathRepository(platformTransactionManager, entityManagerService, shortestPathService);

        // Test
        pathRepository.initData();
        String actualPath = pathRepository.getShortestPath("Pluto");

        //Verify
        assertThat(actualPath, sameBeanAs(path.toString()));
    }
}
