package za.co.discovery.assignment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.discovery.assignment.dao.EdgeDao;
import za.co.discovery.assignment.dao.TrafficDao;
import za.co.discovery.assignment.dao.VertexDao;
import za.co.discovery.assignment.entity.Edge;
import za.co.discovery.assignment.entity.Traffic;
import za.co.discovery.assignment.entity.Vertex;
import za.co.discovery.assignment.helper.Graph;
import za.co.discovery.assignment.helper.GraphMapper;
import za.co.discovery.assignment.model.EdgeModel;
import za.co.discovery.assignment.model.TrafficModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kapeshi.Kongolo on 2016/04/10.
 */
@Service
public class EntityManagerService {
    private VertexDao vertexDao;
    private EdgeDao edgeDao;
    private TrafficDao trafficDao;
    private XLSXHandler excelHandler;

    @Autowired
    public EntityManagerService(VertexDao vertexDao, EdgeDao edgeDao, TrafficDao trafficDao, XLSXHandler excelHandler) {
        this.vertexDao = vertexDao;
        this.edgeDao = edgeDao;
        this.trafficDao = trafficDao;
        this.excelHandler = excelHandler;
    }

    public void readExcelFileAndImportIntoDatabase() {
        Map<String, Edge> edgeMap = new LinkedHashMap();

        Map<String, Vertex> vertexMap = new LinkedHashMap<>(excelHandler.readVertexes());
        List<EdgeModel> edges = new ArrayList<>(excelHandler.readEdges());
        List<TrafficModel> traffics = new ArrayList<>(excelHandler.readTraffics());

        for (EdgeModel edgeModel : edges) {
            GraphMapper mapper = new GraphMapper(vertexMap, edgeModel);
            if (mapper.getSource() != null && mapper.getDestination() != null) {
                Edge edge = new Edge(mapper.createHumanReadableId(edgeModel.getSource(), edgeModel.getDestination()), edgeModel.getWeight());
                mapper.getSource().addSourceEdges(edge);
                mapper.getDestination().addDestinationEdges(edge);
                edgeMap.put(mapper.createHumanReadableId(edgeModel.getSource(), edgeModel.getDestination()), edge);
            }
        }

        for (TrafficModel trafficModel : traffics) {
            GraphMapper mapper = new GraphMapper(edgeMap, trafficModel);
            if (mapper.getEdge() != null) {
                Traffic traffic = new Traffic(mapper.createHumanReadableId(trafficModel.getSource(), trafficModel.getDestination()), trafficModel.getWeight());
                mapper.getEdge().addTraffic(traffic);
            }
        }

        for (Vertex vertex : vertexMap.values()) {
            vertexDao.save(vertex);
        }
    }


    public Graph selectGraph() {
        List<Vertex> vertices = vertexDao.selectAll();
        List<Edge> edges = edgeDao.selectAll();
        List<Traffic> traffics = trafficDao.selectAll();

        return new Graph(vertices, edges, traffics);
    }

    public Vertex saveVertex(Vertex vertex) {
        vertexDao.save(vertex);
        return vertex;
    }

    public Vertex updateVertex(Vertex vertex) {
        vertexDao.update(vertex);
        return vertex;
    }

    public boolean deleteVertex(String id) {
        Vertex vertex = vertexDao.selectUniqueLazyLoad(id);
        vertexDao.delete(vertex);
        return true;
    }

    public List<Vertex> getAllVertices() {
        return vertexDao.selectAll();
    }

    public Vertex getVertexByName(String name) {
        return vertexDao.selectUniqueByName(name);
    }

    public Vertex getVertexById(String vertexId) {
        return vertexDao.selectUnique(vertexId);
    }

    public boolean vertexExist(String vertexId) {
        Vertex vertex = vertexDao.selectUnique(vertexId);
        return vertex != null;
    }

    public Edge saveEdge(Edge edge) {
        edgeDao.save(edge);
        return edge;
    }

    public Edge updateEdge(Edge edge) {
        edgeDao.update(edge);
        return edge;
    }

    public boolean deleteEdge(long id) {
        Edge edge = edgeDao.selectUniqueLazyLoad(id);
        edgeDao.delete(edge);
        return true;
    }

    public List<Edge> getAllEdges() {
        return edgeDao.selectAll();
    }

    public List<Edge> getAllUnusedEdges() {
        return edgeDao.selectAllUnusedEdges();
    }

    public Edge getEdgeById(Long id) {
        return edgeDao.selectUnique(id);
    }

    public boolean edgeExists(Edge edge) {
        List<Edge> edges = edgeDao.edgeExists(edge);
        return !edges.isEmpty();
    }

    public Traffic saveTraffic(Traffic traffic) {
        trafficDao.save(traffic);
        return traffic;
    }

    public Traffic updateTraffic(Traffic traffic) {
        trafficDao.update(traffic);
        return traffic;
    }

    public boolean deleteTraffic(Long id) {
        Traffic traffic = trafficDao.selectUnique(id);
        trafficDao.delete(traffic);
        return true;
    }

    public List<Traffic> getAllTraffics() {
        return trafficDao.selectAllLazyLoading();
    }

    public Traffic getTrafficById(Long id) {
        return trafficDao.selectUnique(id);
    }
}
