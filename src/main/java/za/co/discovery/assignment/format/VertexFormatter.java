package za.co.discovery.assignment.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;
import za.co.discovery.assignment.dao.VertexDao;
import za.co.discovery.assignment.entity.Vertex;

import java.util.Locale;

@Component
public class VertexFormatter implements Formatter<Vertex> {

    @Autowired
    VertexDao vertexDao;

    @Override
    public String print(Vertex object, Locale locale) {
        return object.getName();
    }

    @Override
    public Vertex parse(String id, Locale locale) throws ParseException {
        return vertexDao.selectUnique(id);
    }
}
