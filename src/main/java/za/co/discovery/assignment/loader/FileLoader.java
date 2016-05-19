package za.co.discovery.assignment.loader;

import org.springframework.core.io.Resource;

public class FileLoader {
    private Resource resource;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
