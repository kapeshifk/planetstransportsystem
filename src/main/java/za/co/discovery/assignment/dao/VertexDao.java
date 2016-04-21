package za.co.discovery.assignment.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import za.co.discovery.assignment.entity.Vertex;

import java.util.List;


/**
 * Created by Kapeshi.Kongolo on 2016/04/09.
 */
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

    public int delete(String vertexId) {
        Session session = sessionFactory.getCurrentSession();
        String qry = "DELETE FROM vertex AS V WHERE V.vertexId = :vertexIdParameter";
        Query query = session.createQuery(qry);
        query.setParameter("vertexIdParameter", vertexId);

        return query.executeUpdate();
    }

    public Vertex selectUnique(String vertexId) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Vertex.class);
        criteria.add(Restrictions.eq("vertexId", vertexId));

        return (Vertex) criteria.uniqueResult();
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
