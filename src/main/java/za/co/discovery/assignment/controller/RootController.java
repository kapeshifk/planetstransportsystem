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
import za.co.discovery.assignment.helper.ValidationCodes;
import za.co.discovery.assignment.model.ShortestPathModel;
import za.co.discovery.assignment.service.EntityManagerService;
import za.co.discovery.assignment.service.ShortestPathService;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kapeshi.Kongolo on 2016/04/09.
 */
@Controller
public class RootController {

    private static final String PATH_NOT_AVAILABLE = "Unavailable.";
    private static final String PATH_NOT_NEEDED = "Not needed. You are already on planet ";
    private static final String NO_PLANET_FOUND = "No planet found.";
    private static final String DUPLICATE_ROUTE = "You cannot link a route to itself.";
    private static final String DUPLICATE_TRAFFIC = "You cannot add traffic on the same route origin and destination.";
    private static final String INVALID_CODE = "Failed to find the validation code. Please start again.";
    private EntityManagerService entityManagerService;
    private ShortestPathService shortestPathService;

    @Autowired
    public RootController(EntityManagerService entityManagerService, ShortestPathService shortestPathService) {
        this.entityManagerService = entityManagerService;
        this.shortestPathService = shortestPathService;
    }

    /*Planets Mapping Start*/

    @RequestMapping(value = "/vertices", method = RequestMethod.GET)
    public String listVertices(Model model) {
        List allVertices = entityManagerService.getAllVertices();
        model.addAttribute("vertices", allVertices);
        return "vertices";
    }

    @RequestMapping("vertex/{vertexId}")
    public String showVertex(@PathVariable String vertexId, Model model) {
        model.addAttribute("vertex", entityManagerService.getVertexById(vertexId));
        return "vertexshow";
    }

    @RequestMapping("vertex/new")
    public String addVertex(Model model) {
        model.addAttribute("vertex", new Vertex());
        return "vertexadd";
    }

    @RequestMapping(value = "vertex", method = RequestMethod.POST)
    public String saveVertex(Vertex vertex, Model model) {
        if (entityManagerService.vertexExist(vertex.getVertexId())) {
            buildVertexValidation(vertex.getVertexId(), model);
            return "validation";
        }
        entityManagerService.saveVertex(vertex);
        return "redirect:/vertex/" + vertex.getVertexId();
    }

    @RequestMapping("vertex/edit/{vertexId}")
    public String editVertex(@PathVariable String vertexId, Model model) {
        model.addAttribute("vertex", entityManagerService.getVertexById(vertexId));
        return "vertexupdate";
    }

    @RequestMapping(value = "vertexupdate", method = RequestMethod.POST)
    public String updateVertex(Vertex vertex) {
        entityManagerService.updateVertex(vertex);
        return "redirect:/vertex/" + vertex.getVertexId();
    }

    @RequestMapping("vertex/delete/{vertexId}")
    public String deleteVertex(@PathVariable String vertexId) {
        entityManagerService.deleteVertex(vertexId);
        return "redirect:/vertices";
    }

    public void buildVertexValidation(String vertexId, Model model) {
        //avoid useless nulls
        String vertexName = entityManagerService.getVertexById(vertexId) == null ? "" : entityManagerService.getVertexById(vertexId).getName();
        String message = "Planet " + vertexId + " already exists as " + vertexName;
        model.addAttribute("validationMessage", message);
    }

    /*Planets Mapping End*/

    /*Routes Mapping Start*/

    @RequestMapping(value = "/edges", method = RequestMethod.GET)
    public String listEdges(Model model) {
        List allEdges = entityManagerService.getAllEdges();
        model.addAttribute("edges", allEdges);
        return "edges";
    }

    @RequestMapping("edge/{recordId}")
    public String showEdge(@PathVariable long recordId, Model model) {
        model.addAttribute("edge", entityManagerService.getEdgeById(recordId));
        return "edgeshow";
    }

    @RequestMapping("edge/delete/{recordId}")
    public String deleteEdge(@PathVariable long recordId) {
        entityManagerService.deleteEdge(recordId);
        return "redirect:/edges";
    }

    @RequestMapping(value = "edge/new", method = RequestMethod.GET)
    public String addEdge(Model model) {
        ShortestPathModel sh = new ShortestPathModel();
        List allVertices = entityManagerService.getAllVertices();
        model.addAttribute("edge", new Edge());
        model.addAttribute("edgeModel", sh);
        model.addAttribute("routeList", allVertices);
        return "edgeadd";
    }

    @RequestMapping(value = "edge", method = RequestMethod.POST)
    public String saveEdge(Edge edge, @ModelAttribute ShortestPathModel pathModel, Model model) {
        int id = (int) entityManagerService.getEdgeMaxRecordId() + 1;
        edge.setRecordId(id);
        edge.setEdgeId(String.valueOf(id));
        edge.setSource(pathModel.getSourceVertex());
        edge.setDestination(pathModel.getDestinationVertex());
        if (pathModel.getSourceVertex().equals(pathModel.getDestinationVertex())) {
            buildEdgeValidation(pathModel, model, ValidationCodes.ROUTE_TO_SELF.toString());
            return "validation";
        }
        if (entityManagerService.edgeExists(edge)) {
            buildEdgeValidation(pathModel, model, ValidationCodes.ROUTE_EXISTS.toString());
            return "validation";
        }
        entityManagerService.saveEdge(edge);
        return "redirect:/edge/" + edge.getRecordId();
    }

    @RequestMapping(value = "edge/edit/{recordId}", method = RequestMethod.GET)
    public String editEdge(@PathVariable long recordId, Model model) {
        ShortestPathModel pathModel = new ShortestPathModel();
        List allVertices = entityManagerService.getAllVertices();
        Edge edgeToEdit = entityManagerService.getEdgeById(recordId);
        pathModel.setSourceVertex(edgeToEdit.getSource());
        pathModel.setDestinationVertex(edgeToEdit.getDestination());
        model.addAttribute("edge", edgeToEdit);
        model.addAttribute("edgeModel", pathModel);
        model.addAttribute("routeList", allVertices);
        return "edgeupdate";
    }

    @RequestMapping(value = "edgeupdate", method = RequestMethod.POST)
    public String updateEdge(Edge edge, @ModelAttribute ShortestPathModel pathModel, Model model) {
        edge.setSource(pathModel.getSourceVertex());
        edge.setDestination(pathModel.getDestinationVertex());
        if (pathModel.getSourceVertex().equals(pathModel.getDestinationVertex())) {
            buildEdgeValidation(pathModel, model, ValidationCodes.ROUTE_TO_SELF.toString());
            return "validation";
        }

        if (entityManagerService.edgeExists(edge)) {
            buildEdgeValidation(pathModel, model, ValidationCodes.ROUTE_EXISTS.toString());
            return "validation";
        }
        entityManagerService.updateEdge(edge);
        return "redirect:/edge/" + edge.getRecordId();
    }

    public void buildEdgeValidation(@ModelAttribute ShortestPathModel pathModel, Model model, String code) {
        String message = "";
        ValidationCodes mode = ValidationCodes.fromString(code);
        if (mode != null) {
            switch (mode) {
                case ROUTE_EXISTS:
                    String sourceName = entityManagerService.getVertexById(pathModel.getSourceVertex()) == null ? "" : entityManagerService.getVertexById(pathModel.getSourceVertex()).getName();
                    String sourceDestination = entityManagerService.getVertexById(pathModel.getDestinationVertex()) == null ? "" : entityManagerService.getVertexById(pathModel.getDestinationVertex()).getName();
                    message = "The route from " + sourceName + " (" + pathModel.getSourceVertex() + ") to " + sourceDestination + "(" + pathModel.getDestinationVertex() + ") exists already.";
                    break;
                case ROUTE_TO_SELF:
                    message = DUPLICATE_ROUTE;
                    break;
                default:
                    message = INVALID_CODE;
                    break;
            }
        }
        //
        model.addAttribute("validationMessage", message);
    }

    /*Routes Mapping End*/

    /*Traffics Mapping Start*/

    @RequestMapping(value = "/traffics", method = RequestMethod.GET)
    public String listTraffics(Model model) {
        List<Traffic> allTraffics = entityManagerService.getAllTraffics();
        model.addAttribute("traffics", allTraffics);
        return "traffics";
    }

    @RequestMapping("traffic/{routeId}")
    public String showTraffic(@PathVariable String routeId, Model model) {
        model.addAttribute("traffic", entityManagerService.getTrafficById(routeId));
        return "trafficshow";
    }

    @RequestMapping("traffic/delete/{routeId}")
    public String deleteTraffic(@PathVariable String routeId) {
        entityManagerService.deleteTraffic(routeId);
        return "redirect:/traffics";
    }

    @RequestMapping(value = "traffic/new", method = RequestMethod.GET)
    public String addTraffic(Model model) {
        ShortestPathModel sh = new ShortestPathModel();
        List allVertices = entityManagerService.getAllVertices();
        model.addAttribute("traffic", new Traffic());
        model.addAttribute("trafficModel", sh);
        model.addAttribute("trafficList", allVertices);
        return "trafficadd";
    }

    @RequestMapping(value = "traffic", method = RequestMethod.POST)
    public String saveTraffic(Traffic traffic, @ModelAttribute ShortestPathModel pathModel, Model model) {
        int id = (int) entityManagerService.getTrafficMaxRecordId() + 1;
        traffic.setRouteId(String.valueOf(id));
        traffic.setSource(pathModel.getSourceVertex());
        traffic.setDestination(pathModel.getDestinationVertex());
        if (pathModel.getSourceVertex().equals(pathModel.getDestinationVertex())) {
            buildTrafficValidation(pathModel, model, ValidationCodes.TRAFFIC_TO_SELF.toString());
            return "validation";
        }
        if (entityManagerService.trafficExists(traffic)) {
            buildTrafficValidation(pathModel, model, ValidationCodes.TRAFFIC_EXISTS.toString());
            return "validation";
        }
        entityManagerService.saveTraffic(traffic);
        return "redirect:/traffic/" + traffic.getRouteId();
    }

    @RequestMapping(value = "traffic/edit/{routeId}", method = RequestMethod.GET)
    public String editTraffic(@PathVariable String routeId, Model model) {
        ShortestPathModel pathModel = new ShortestPathModel();
        List allVertices = entityManagerService.getAllVertices();
        Traffic trafficToEdit = entityManagerService.getTrafficById(routeId);
        pathModel.setSourceVertex(trafficToEdit.getSource());
        pathModel.setDestinationVertex(trafficToEdit.getDestination());
        model.addAttribute("traffic", trafficToEdit);
        model.addAttribute("trafficModel", pathModel);
        model.addAttribute("trafficList", allVertices);
        return "trafficupdate";
    }

    @RequestMapping(value = "trafficupdate", method = RequestMethod.POST)
    public String updateTraffic(Traffic traffic, @ModelAttribute ShortestPathModel pathModel, Model model) {
        traffic.setSource(pathModel.getSourceVertex());
        traffic.setDestination(pathModel.getDestinationVertex());
        if (pathModel.getSourceVertex().equals(pathModel.getDestinationVertex())) {
            buildTrafficValidation(pathModel, model, ValidationCodes.TRAFFIC_TO_SELF.toString());
            return "validation";
        }
        if (entityManagerService.trafficExists(traffic)) {
            buildTrafficValidation(pathModel, model, ValidationCodes.TRAFFIC_EXISTS.toString());
            return "validation";
        }
        entityManagerService.updateTraffic(traffic);
        return "redirect:/traffic/" + traffic.getRouteId();
    }

    public void buildTrafficValidation(@ModelAttribute ShortestPathModel pathModel, Model model, String code) {
        String message = "";
        ValidationCodes mode = ValidationCodes.fromString(code);
        if (mode != null) {
            switch (mode) {
                case TRAFFIC_EXISTS:
                    String sourceName = entityManagerService.getVertexById(pathModel.getSourceVertex()) == null ? "" : entityManagerService.getVertexById(pathModel.getSourceVertex()).getName();
                    String sourceDestination = entityManagerService.getVertexById(pathModel.getDestinationVertex()) == null ? "" : entityManagerService.getVertexById(pathModel.getDestinationVertex()).getName();
                    message = "The traffic from " + sourceName + " (" + pathModel.getSourceVertex() + ") to " + sourceDestination + " (" + pathModel.getDestinationVertex() + ") exists already.";
                    break;
                case TRAFFIC_TO_SELF:
                    message = DUPLICATE_TRAFFIC;
                    break;
                default:
                    message = INVALID_CODE;
                    break;
            }
        }
        //
        model.addAttribute("validationMessage", message);
    }
    /*Traffics Mapping End*/

    /*Shortest Path Mapping Start*/

    @RequestMapping(value = "/shortest", method = RequestMethod.GET)
    public String shortestForm(Model model) {
        ShortestPathModel pathModel = new ShortestPathModel();
        List<Vertex> allVertices = entityManagerService.getAllVertices();
        if (allVertices == null || allVertices.isEmpty()) {
            model.addAttribute("validationMessage", NO_PLANET_FOUND);
            return "validation";
        }
        Vertex origin = allVertices.get(0);
        pathModel.setVertexName(origin.getName());
        model.addAttribute("shortest", pathModel);
        model.addAttribute("pathList", allVertices);
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
        Vertex source = entityManagerService.getVertexByName(pathModel.getVertexName());
        Vertex destination = entityManagerService.getVertexById(pathModel.getSelectedVertex());
        //
        shortestPathService.run(source);
        LinkedList<Vertex> paths = shortestPathService.getPath(destination);
        if (paths != null) {
            for (Vertex v : paths) {
                path.append(v.getName() + " (" + v.getVertexId() + ")");
                path.append("\t");
            }
        } else if (source != null && destination != null && source.getVertexId().equals(destination.getVertexId())) {
            path.append(PATH_NOT_NEEDED + source.getName());
        } else {
            path.append(PATH_NOT_AVAILABLE);
        }
        pathModel.setThePath(path.toString());
        pathModel.setSelectedVertexName(destination.getName());
        model.addAttribute("shortest", pathModel);
        return "result";
    }

    /*Shortest Path Mapping End*/
}

