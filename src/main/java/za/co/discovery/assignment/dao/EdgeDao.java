package za.co.discovery.assignment.dao;

import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import za.co.discovery.assignment.entity.Edge;

import java.util.List;

/**
 * Created by Kapeshi.Kongolo on 2016/04/09.
 */
@Repository
@Transactional
public class EdgeDao {

    private SessionFactory sessionFactory;

    @Autowired
    public EdgeDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    public void save(Edge edge) {
        Session session = sessionFactory.getCurrentSession();
        session.save(edge);
    }

    public void update(Edge edge) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(edge);
    }

    public void delete(Edge edge) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(edge);
    }

    public int findNextId() {
        Session session = sessionFactory.getCurrentSession();
        String qry = "values ( next value for EDGE_SEQ )";
        return (int) session.createSQLQuery(qry).uniqueResult();
    }

    public Edge selectUnique(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Edge.class);
        criteria.add(Restrictions.eq("id", id));

        return (Edge) criteria.uniqueResult();
    }

    public Edge selectUniqueLazyLoad(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Edge.class);
        criteria.add(Restrictions.eq("id", id));
        Edge edge = (Edge) criteria.uniqueResult();
        if (edge != null) {
            Hibernate.initialize(edge.getSource().getSourceEdges());
            Hibernate.initialize(edge.getDestination().getDestinationEdges());
        }
        return (Edge) criteria.uniqueResult();
    }

    public List<Edge> edgeExists(Edge edge) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "FROM edge AS E WHERE E.source = :source AND E.destination = :destination and E.id!=:id";
        Query query = session.createQuery(hql);
        query.setEntity("source", edge.getSource());
        query.setEntity("destination", edge.getDestination());
        query.setParameter("id", edge.getId());
        return query.list();
    }

    public List<Edge> selectAllByRecordId(long id) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Edge.class);
        criteria.add(Restrictions.eq("id", id));

        return criteria.list();
    }

    public List<Edge> selectAllByRouteId(String routeId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Edge.class);
        criteria.add(Restrictions.eq("routeId", routeId));

        return criteria.list();
    }

    public List<Edge> selectAll() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Edge.class);
        return criteria.list();
    }

    public List<Edge> selectAllUnusedEdges() {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT e FROM edge e LEFT JOIN FETCH e.traffic WHERE e.traffic.id is null";
        Query query = session.createQuery(hql);
        return query.list();
    }
}

