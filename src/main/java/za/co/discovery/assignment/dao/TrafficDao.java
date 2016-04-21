package za.co.discovery.assignment.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import za.co.discovery.assignment.entity.Traffic;

import java.util.List;

/**
 * Created by Kapeshi.Kongolo on 2016/04/09.
 */
@Repository
@Transactional
public class TrafficDao {

    private SessionFactory sessionFactory;

    @Autowired
    public TrafficDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Traffic traffic) {
        Session session = sessionFactory.getCurrentSession();
        session.save(traffic);
    }

    public void update(Traffic traffic) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(traffic);
    }

    public int delete(String routeId) {
        Session session = sessionFactory.getCurrentSession();
        String qry = "DELETE FROM traffic AS T WHERE T.routeId = :routeIdParameter";
        Query query = session.createQuery(qry);
        query.setParameter("routeIdParameter", routeId);

        return query.executeUpdate();
    }

    public Traffic selectUnique(String routeId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Traffic.class);
        criteria.add(Restrictions.eq("routeId", routeId));

        return (Traffic) criteria.uniqueResult();
    }

    public List<Traffic> selectAll() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Traffic.class);

        //noinspection unchecked
        return (List<Traffic>) criteria.list();
    }

    public long selectMaxRecordId() {

        return (long) sessionFactory.getCurrentSession()
                .createCriteria(Traffic.class)
                .setProjection(Projections.rowCount()).uniqueResult();
    }

    public List<Traffic> trafficExists(Traffic traffic) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Traffic.class);
        criteria.add(Restrictions.ne("routeId", traffic.getRouteId()));
        criteria.add(Restrictions.eq("source", traffic.getSource()));
        criteria.add(Restrictions.eq("destination", traffic.getDestination()));

        //noinspection unchecked
        return (List<Traffic>) criteria.list();
    }
}
