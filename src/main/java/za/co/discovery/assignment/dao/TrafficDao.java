package za.co.discovery.assignment.dao;

import org.hibernate.*;
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

    public void delete(Traffic traffic) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(traffic);
    }

    public Traffic selectUnique(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Traffic.class);
        criteria.add(Restrictions.eq("id", id));

        Traffic traffic = (Traffic) criteria.uniqueResult();
        if (traffic != null) {
            Hibernate.initialize(traffic.getRoute());
        }
        return traffic;
    }

    public Traffic selectUniqueByRouteId(String routeId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Traffic.class);
        criteria.add(Restrictions.eq("routeId", routeId));

        Traffic traffic = (Traffic) criteria.uniqueResult();
        if (traffic != null) {
            Hibernate.initialize(traffic.getRoute());
        }
        return traffic;
    }

    public int findNextId() {
        Session session = sessionFactory.getCurrentSession();
        String qry = "values ( next value for TRAFFIC_SEQ )";
        return (int) session.createSQLQuery(qry).uniqueResult();
    }

    public List<Traffic> selectAll() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Traffic.class);

        //noinspection unchecked
        return (List<Traffic>) criteria.list();
    }

    public List<Traffic> selectAllLazyLoading() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM traffic AS T LEFT JOIN FETCH T.route";
        Query query = session.createQuery(hql);
        return query.list();
    }
}
