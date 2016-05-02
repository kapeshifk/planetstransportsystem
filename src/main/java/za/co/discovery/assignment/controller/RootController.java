package za.co.discovery.assignment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import za.co.discovery.assignment.entity.Edge;
import za.co.discovery.assignment.entity.Traffic;
import za.co.discovery.assignment.entity.Vertex;
import za.co.discovery.assignment.helper.Graph;
import za.co.discovery.assignment.helper.GraphMapper;
import za.co.discovery.assignment.model.ShortestPathModel;
import za.co.discovery.assignment.service.EntityManagerService;
import za.co.discovery.assignment.service.ShortestPathService;

import java.util.LinkedList;
import java.util.List;

@Controller
public class RootController {

    private static final String PATH_NOT_AVAILABLE = "Unavailable.";
    private static final String PATH_NOT_NEEDED = "Not needed. You are already on planet ";
    private static final String NO_PLANET_FOUND = "No planet found.";
    private static final String DUPLICATE_ROUTE = "You cannot link a route to itself.";
    private EntityManagerService entityManagerService;
    private ShortestPathService shortestPathService;

    @Autowired
    public RootController(EntityManagerService entityManagerService, ShortestPathService shortestPathService) {
        this.entityManagerService = entityManagerService;
        this.shortestPathService = shortestPathService;
    }

    //Planets Mapping Start

    @RequestMapping(value = "/vertices", method = RequestMethod.GET)
    public String listVertices(Model model) {
        List vertices = entityManagerService.getAllVertices();
        model.addAttribute("vertices", vertices);
        return "vertices";
    }

    @RequestMapping("vertex/{id}")
    public String viewVertex(@PathVariable String id, Model model) {
        model.addAttribute("vertex", entityManagerService.getVertexById(id));
        return "vertex_view";
    }

    @RequestMapping("vertex/add")
    public String addVertex(Model model) {
        model.addAttribute("vertex", new Vertex());
        return "vertex_add";
    }

    @RequestMapping(value = "save_vertex", method = RequestMethod.POST)
    public String saveVertex(Vertex vertex, Model model) {
        if (entityManagerService.vertexExist(vertex.getId())) {
            buildVertexValidation(vertex.getId(), model);
            return "validation";
        }
        entityManagerService.saveVertex(vertex);
        return "redirect:/vertex/" + vertex.getId();
    }

    @RequestMapping("vertex/edit/{id}")
    public String editVertex(@PathVariable String id, Model model) {
        model.addAttribute("vertex", entityManagerService.getVertexById(id));
        return "vertex_update";
    }

    @RequestMapping(value = "update_vertex", method = RequestMethod.POST)
    public String updateVertex(Vertex vertex) {
        entityManagerService.updateVertex(vertex);
        return "redirect:/vertex/" + vertex.getId();
    }

    @RequestMapping("vertex/delete/{vertexId}")
    public String deleteVertex(@PathVariable String vertexId) {
        entityManagerService.deleteVertex(vertexId);
        return "redirect:/vertices";
    }

    private void buildVertexValidation(String vertexId, Model model) {
        String vertexName = entityManagerService.getVertexById(vertexId) == null ? "" : entityManagerService.getVertexById(vertexId).getName();
        String message = "Planet " + vertexId + " already exists as " + vertexName;
        model.addAttribute("validationMessage", message);
    }
    //Planets Mapping End

    //Routes Mapping Start
    @RequestMapping(value = "/edges", method = RequestMethod.GET)
    public String listEdges(Model model) {
        List edges = entityManagerService.getAllEdges();
        model.addAttribute("edges", edges);
        return "edges";
    }

    @RequestMapping("edge/{id}")
    public String viewEdge(@PathVariable long id, Model model) {
        model.addAttribute("edge", entityManagerService.getEdgeById(id));
        return "edge_view";
    }

    @RequestMapping(value = "edge/add", method = RequestMethod.GET)
    public String addEdge(Model model) {
        ShortestPathModel sh = new ShortestPathModel();
        List vertices = entityManagerService.getAllVertices();
        model.addAttribute("edge", new Edge());
        model.addAttribute("edgeModel", sh);
        model.addAttribute("routeList", vertices);
        return "edge_add";
    }

    @RequestMapping(value = "save_edge", method = RequestMethod.POST)
    public String saveEdge(Edge edge, @ModelAttribute ShortestPathModel pathModel, Model model) {
        GraphMapper mapper = new GraphMapper();
        edge.setSource(pathModel.getSourceVertex());
        edge.setDestination(pathModel.getDestinationVertex());

        if (pathModel.getSourceVertex().equals(pathModel.getDestinationVertex())) {
            String message = DUPLICATE_ROUTE;
            model.addAttribute("validationMessage", message);
            return "validation";
        }
        if (entityManagerService.edgeExists(edge)) {
            buildEdgeValidation(pathModel, model);
            return "validation";
        }
        edge.setRouteId(mapper.createHumanReadableId(edge.getSource().getId(), edge.getDestination().getId()));
        entityManagerService.saveEdge(edge);
        return "redirect:/edge/" + edge.getId();
    }

    @RequestMapping(value = "edge/edit/{id}", method = RequestMethod.GET)
    public String editEdge(@PathVariable long id, Model model) {
        ShortestPathModel pathModel = new ShortestPathModel();
        List vertices = entityManagerService.getAllVertices();
        Edge edgeToEdit = entityManagerService.getEdgeById(id);
        System.out.println("BEFORE ");
        System.out.println("THE TRAFFIC ON ROUTE " + edgeToEdit.getRouteId() + " IS " + edgeToEdit.getTraffic().getDelay());
        pathModel.setSourceVertex(edgeToEdit.getSource());
        pathModel.setDestinationVertex(edgeToEdit.getDestination());
        model.addAttribute("edge", edgeToEdit);
        model.addAttribute("edgeModel", pathModel);
        model.addAttribute("routeList", vertices);
        return "edge_update";
    }

    @RequestMapping(value = "update_edge", method = RequestMethod.POST)
    public String updateEdge(@ModelAttribute Edge edge, @ModelAttribute ShortestPathModel pathModel, Model model) {
        GraphMapper mapper = new GraphMapper();
        edge.setSource(pathModel.getSourceVertex());
        edge.setDestination(pathModel.getDestinationVertex());
        System.out.println("AFTER");
        System.out.println("THE TRAFFIC ON ROUTE " + edge.getRouteId() + " IS STILL " + edge.getTraffic().getDelay());
        if (pathModel.getSourceVertex().equals(pathModel.getDestinationVertex())) {
            String message = DUPLICATE_ROUTE;
            model.addAttribute("validationMessage", message);
            return "validation";
        }

        if (entityManagerService.edgeExists(edge)) {
            buildEdgeValidation(pathModel, model);
            return "validation";
        }

        edge.setRouteId(mapper.createHumanReadableId(edge.getSource().getId(), edge.getDestination().getId()));
        System.out.println("STILL 1 " + edge.getTraffic());
        entityManagerService.updateEdge(edge);
        System.out.println("STILL 2 " + edge.getTraffic());
        return "redirect:/edge/" + edge.getId();
    }

    @RequestMapping("edge/delete/{id}")
    public String deleteEdge(@PathVariable long id) {
        entityManagerService.deleteEdge(id);
        return "redirect:/edges";
    }

    private void buildEdgeValidation(@ModelAttribute ShortestPathModel pathModel, Model model) {
        String message = "The route from " + pathModel.getSourceVertex().getName() + " (" + pathModel.getSourceVertex().getId() + ") to " + pathModel.getDestinationVertex().getName() + "(" + pathModel.getDestinationVertex().getId() + ") exists already.";
        model.addAttribute("validationMessage", message);
        model.addAttribute("validationMessage", message);
    }
    //Routes Mapping End

    //Traffics Mapping Start
    @RequestMapping(value = "/traffics", method = RequestMethod.GET)
    public String listTraffics(Model model) {
        List allTraffics = entityManagerService.getAllTraffics();
        model.addAttribute("traffics", allTraffics);
        return "traffics";
    }

    @RequestMapping("traffic/{id}")
    public String viewTraffic(@PathVariable Long id, Model model) {
        model.addAttribute("traffic", entityManagerService.getTrafficById(id));
        return "traffic_view";
    }

    @RequestMapping(value = "traffic/add", method = RequestMethod.GET)
    public String addTraffic(Model model) {
        ShortestPathModel sh = new ShortestPathModel();
        List edges = entityManagerService.getAllUnusedEdges();
        model.addAttribute("traffic", new Traffic());
        model.addAttribute("trafficModel", sh);
        model.addAttribute("trafficList", edges);
        return "traffic_add";
    }

    @RequestMapping(value = "save_traffic", method = RequestMethod.POST)
    public String saveTraffic(Traffic traffic, @ModelAttribute ShortestPathModel pathModel, Model model) {
        GraphMapper mapper = new GraphMapper();
        traffic.setRoute(pathModel.getSelectedEdge());
        traffic.setRouteId(mapper.createHumanReadableId(traffic.getRoute().getSource().getId(), traffic.getRoute().getDestination().getId()));
        entityManagerService.saveTraffic(traffic);
        return "redirect:/traffic/" + traffic.getId();
    }

    @RequestMapping(value = "traffic/edit/{id}", method = RequestMethod.GET)
    public String editTraffic(@PathVariable Long id, Model model) {
        ShortestPathModel pathModel = new ShortestPathModel();
        List edges = entityManagerService.getAllEdges();
        Traffic trafficToEdit = entityManagerService.getTrafficById(id);
        pathModel.setSelectedEdge(trafficToEdit.getRoute());
        model.addAttribute("traffic", trafficToEdit);
        model.addAttribute("trafficModel", pathModel);
        model.addAttribute("trafficList", edges);
        return "traffic_update";
    }

    @RequestMapping(value = "update_traffic", method = RequestMethod.POST)
    public String updateTraffic(Traffic traffic, @ModelAttribute ShortestPathModel pathModel, Model model) {
        entityManagerService.updateTraffic(traffic);
        return "redirect:/traffic/" + traffic.getId();
    }

    @RequestMapping("traffic/delete/{id}")
    public String deleteTraffic(@PathVariable Long id) {
        entityManagerService.deleteTraffic(id);
        return "redirect:/traffics";
    }
    //Traffics Mapping End

    // Shortest Path Mapping Start

    @RequestMapping(value = "/shortest", method = RequestMethod.GET)
    public String shortestForm(Model model) {
        ShortestPathModel pathModel = new ShortestPathModel();
        List<Vertex> vertices = entityManagerService.getAllVertices();
        if (vertices == null || vertices.isEmpty()) {
            model.addAttribute("validationMessage", NO_PLANET_FOUND);
            return "validation";
        }
        Vertex origin = vertices.get(0);
        pathModel.setVertexName(origin.getName());
        pathModel.setVertexId(origin.getId());
        model.addAttribute("shortest", pathModel);
        model.addAttribute("pathList", vertices);
        return "shortest";
    }

    @RequestMapping(value = "/shortest", method = RequestMethod.POST)
    public String shortestSubmit(@ModelAttribute ShortestPathModel pathModel, Model model) {

        StringBuilder path = new StringBuilder();
        Graph graph = entityManagerService.selectGraph();
        if (pathModel.isTrafficAllowed()) {
            graph.setTrafficAllowed(true);
        }
        if (pathModel.isUndirectedGraph()) {
            graph.setUndirectedGraph(true);
        }
        shortestPathService.initializePlanets(graph);
        Vertex source = entityManagerService.getVertexById(pathModel.getVertexId());
        Vertex destination = pathModel.getSelectedVertex();
        //
        shortestPathService.run(source);
        LinkedList<Vertex> paths = shortestPathService.getPath(destination);
        if (paths != null) {
            for (Vertex v : paths) {
                path.append(v.getName()).append(" (").append(v.getId()).append(")");
                path.append("\t");
            }
        } else if (source != null && destination != null && source.getId().equals(destination.getId())) {
            path.append(PATH_NOT_NEEDED).append(source.getName());
        } else {
            path.append(PATH_NOT_AVAILABLE);
        }
        pathModel.setThePath(path.toString());
        pathModel.setSelectedVertexName(destination == null ? "" : destination.getName());
        model.addAttribute("shortest", pathModel);
        return "result";
    }
    //Shortest Path Mapping End
}

