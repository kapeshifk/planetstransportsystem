package za.co.discovery.assignment.dao;

import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import za.co.discovery.assignment.entity.Vertex;

import java.util.List;


@Repository
@Transactional
public class VertexDao {

    private SessionFactory sessionFactory;

    @Autowired
    public VertexDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Vertex vertex) {
        Session session = sessionFactory.getCurrentSession();
        session.save(vertex);
    }

    public void update(Vertex vertex) {
        Session session = sessionFactory.getCurrentSession();
        session.merge(vertex);
    }

    public int delete(String id) {
        Session session = sessionFactory.getCurrentSession();
        String qry = "DELETE FROM vertex AS V WHERE V.id = :idParameter";
        Query query = session.createQuery(qry);
        query.setParameter("idParameter", id);

        return query.executeUpdate();
    }

    public void delete(Vertex vertex) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(vertex);
    }

    public Vertex selectUnique(String id) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Vertex.class);
        criteria.add(Restrictions.eq("id", id));

        return (Vertex) criteria.uniqueResult();
    }

    public Vertex selectUniqueLazyLoad(String id) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Vertex.class);
        criteria.add(Restrictions.eq("id", id));

        Vertex vertex = (Vertex) criteria.uniqueResult();
        if (vertex != null) {
            Hibernate.initialize(vertex.getSourceEdges());
            Hibernate.initialize(vertex.getDestinationEdges());
        }
        return vertex;
    }

    public Vertex selectUniqueByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Vertex.class);
        criteria.add(Restrictions.eq("name", name));

        return (Vertex) criteria.uniqueResult();
    }

    public List<Vertex> selectAll() {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Vertex.class);
        List<Vertex> vertices = (List<Vertex>) criteria.list();

        return vertices;
    }
}
