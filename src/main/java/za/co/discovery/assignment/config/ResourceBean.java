package za.co.discovery.assignment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class ResourceBean {
    private static final String EXCEL_FILENAME = "/interstellar.xlsx";

    @Bean
    public File getFileResource() {
        URL resource = getClass().getResource(EXCEL_FILENAME);
        File file = null;
        try {
            file = new File(resource.toURI());
        } catch (Exception e) {
            Logger.getLogger("discovery").log(Level.SEVERE, "Failed to read the excel file " + EXCEL_FILENAME + ", Exit Interstellar Transport System!");
            System.exit(1);
        }
        return file;
    }
}
