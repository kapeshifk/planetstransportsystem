package za.co.discovery.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import za.co.discovery.assignment.entity.Vertex;
import za.co.discovery.assignment.helper.Graph;
import za.co.discovery.assignment.service.EntityManagerService;
import za.co.discovery.assignment.service.ShortestPathService;

import javax.annotation.PostConstruct;
import java.util.LinkedList;

/**
 * Created by Kapeshi.Kongolo on 2016/04/13.
 */
@Component
public class ShortestPathRepository {

    private static final String PATH_NOT_AVAILABLE = "There is no path to ";
    private static final String PATH_NOT_NEEDED = "Not needed. You are already on planet ";
    private static final String NO_PLANET_FOUND = "No planet found.";
    private static final String PLANET_DOES_NOT_EXIST = " does not exist in the Interstellar Transport System.";
    protected PlatformTransactionManager platformTransactionManager;
    private Graph graph;
    private EntityManagerService entityManagerService;

    @Autowired
    public ShortestPathRepository(@Qualifier("transactionManager") PlatformTransactionManager platformTransactionManager, EntityManagerService entityManagerService) {
        this.platformTransactionManager = platformTransactionManager;
        this.entityManagerService = entityManagerService;
    }

    @PostConstruct
    public void initData() {

        TransactionTemplate tmpl = new TransactionTemplate(platformTransactionManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                entityManagerService.persistGraph();
            }
        });
    }

    public String getShortestPath(String name) {
        StringBuilder path = new StringBuilder();
        graph = entityManagerService.selectGraph();
        ShortestPathService sp = new ShortestPathService(graph);

        if (graph == null || graph.getVertexes() == null || graph.getVertexes().isEmpty()) {
            return NO_PLANET_FOUND;
        }
        Vertex source = graph.getVertexes().get(0);
        Vertex destination = entityManagerService.getVertexByName(name);
        if (destination == null) {
            destination = entityManagerService.getVertexById(name);
            if (destination == null) {
                return name + PLANET_DOES_NOT_EXIST;
            }
        } else if (source != null && destination != null && source.getVertexId().equals(destination.getVertexId())) {
            return PATH_NOT_NEEDED + source.getName() + ".";
        }

        sp.run(source);
        LinkedList<Vertex> paths = sp.getPath(destination);
        if (paths != null) {
            for (Vertex v : paths) {
                path.append(v.getName() + " (" + v.getVertexId() + ")");
                path.append("\t");
            }
        } else {
            path.append(PATH_NOT_AVAILABLE + destination.getName());
            path.append(".");
        }

        return path.toString();
    }
}
