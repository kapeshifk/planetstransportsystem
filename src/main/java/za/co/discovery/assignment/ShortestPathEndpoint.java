package za.co.discovery.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import za.co.discovery.assignment.schema.GetShortestPathRequest;
import za.co.discovery.assignment.schema.GetShortestPathResponse;

/**
 * Created by Kapeshi.Kongolo on 2016/04/13.
 */
@Endpoint
public class ShortestPathEndpoint {
    private static final String NAMESPACE_URI = "http://spring.io/guides/gs-producing-web-service";
    private ShortestPathRepository pathRepository;

    @Autowired
    public ShortestPathEndpoint(ShortestPathRepository pathRepository) {
        this.pathRepository = pathRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getShortestPathRequest")

    @ResponsePayload
    public GetShortestPathResponse getShortestPath(@RequestPayload GetShortestPathRequest request) {
        GetShortestPathResponse response = new GetShortestPathResponse();
        response.setPath(pathRepository.getShortestPath(request.getName()));

        return response;
    }
}