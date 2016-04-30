package za.co.discovery.assignment.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;
import za.co.discovery.assignment.dao.TrafficDao;
import za.co.discovery.assignment.entity.Traffic;

import java.util.Locale;

@Component
public class TrafficFormatter implements Formatter<Traffic> {

    @Autowired
    TrafficDao trafficDao;

    @Override
    public String print(Traffic object, Locale locale) {
        System.out.println("GIVE ME ------- ------- " + object);
        return object.getRouteId();
    }

    @Override
    public Traffic parse(String id, Locale locale) throws ParseException {
        System.out.println("SOME OF THAT =================== " + id);
        return trafficDao.selectUnique(Long.valueOf(id));
    }
}
