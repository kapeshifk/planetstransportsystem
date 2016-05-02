package za.co.discovery.assignment.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;
import za.co.discovery.assignment.dao.EdgeDao;
import za.co.discovery.assignment.entity.Edge;

import java.util.Locale;

@Component
public class EdgeFormatter implements Formatter<Edge> {

    @Autowired
    EdgeDao edgeDao;

    @Override
    public String print(Edge object, Locale locale) {
        return String.valueOf(object.getId());
    }

    @Override
    public Edge parse(String id, Locale locale) throws ParseException {
        return edgeDao.selectUnique(Long.valueOf(id));
    }
}
