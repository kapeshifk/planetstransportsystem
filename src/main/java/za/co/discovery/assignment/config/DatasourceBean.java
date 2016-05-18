package za.co.discovery.assignment.config;

import org.apache.derby.jdbc.EmbeddedDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatasourceBean {

    public DataSource dataSource() {
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        dataSource.setConnectionAttributes("create=true");
        dataSource.setDatabaseName("TransportSystemDB");
        dataSource.setUser("username");
        dataSource.setPassword("password");

        try {
            dataSource.getConnection();
        } catch (SQLException e) {
            Logger.getLogger("discovery").log(Level.SEVERE, "Failed to connect to the database: " + e);
        }

        return dataSource;
    }
}