package za.co.discovery.assignment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import za.co.discovery.assignment.endpoint.ShortestPathEndpoint;
import za.co.discovery.assignment.schema.GetShortestPathRequest;
import za.co.discovery.assignment.schema.GetShortestPathResponse;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/persistence-config.xml", "/spring/services-config.xml", "/spring/endpoint-config.xml", "/spring/import-config.xml"})
public class ShortestPathEndpointTest {

    @Autowired
    private ShortestPathEndpoint shortestPathEndpoint;

    @Test
    public void verifyThatShortestPathSOAPEndPointIsCorrect() throws Exception {
        // Set Up Fixture
        GetShortestPathRequest shortestPathRequest = new GetShortestPathRequest();
        shortestPathRequest.setName("Moon");

        StringBuilder path = new StringBuilder();
        path.append("Earth (A)\tMoon (B)\t");

        GetShortestPathResponse expectedResponse = new GetShortestPathResponse();
        expectedResponse.setPath(path.toString());

        //Test
        GetShortestPathResponse actualResponse = shortestPathEndpoint.getShortestPath(shortestPathRequest);

        // Verify
        assertThat(actualResponse, sameBeanAs(expectedResponse));
        assertThat(actualResponse.getPath(), sameBeanAs("Earth (A)\tMoon (B)\t"));
    }

}
