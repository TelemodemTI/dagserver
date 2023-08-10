package main.infra.adapters.confs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
public class DAO
{
	@Autowired
	private SessionFactory sessionFactory;
	
	private final static Logger logger = Logger.getLogger(DAO.class);
	
	@SuppressWarnings("unchecked")
	@Transactional
    public <T> T save(T o){
        try(Session session = sessionFactory.openSession();) {
        	if(!session.contains(o)){
        		o = (T) session.merge(o);
        	}
        	Transaction tx = null;
        	tx = session.beginTransaction();
    		session.saveOrUpdate(o);
    		tx.commit();
        } catch (HibernateException ex) {
        	ex.printStackTrace();
        } 
        return o;
    }
    public <T> void delete(Object object){
        try(Session session = sessionFactory.openSession();) {
            if(!session.contains(object)){
            	object = session.merge(object);
        	}
            Transaction tx = null;
			tx = session.beginTransaction();
			session.delete(object);
			tx.commit();
        } catch (HibernateException ex) {
        	ex.printStackTrace();
        } 
    }
    public void execute (final String query, Map<String,Object> params){
        try(Session session = this.getCreateSession();) {
        	Query queryO = session.createQuery(query);
        	var keys = params.keySet();
        	for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				String string = iterator.next();
				queryO.setParameter(string, params.get(string));	
			}
            queryO.executeUpdate();
        } catch (HibernateException e) {
        	logger.error(e);
        } 
    }
    private Session getCreateSession() {
    	try {
      	    return sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
      	    return sessionFactory.openSession();
        }
    }
    public <T> List<T> read(final Class<T> returnType,final String query ){
    	return this.read(returnType, query,new HashMap<String,Object>(){
			private static final long serialVersionUID = 1L;
		{}},null,null);
    }
    public <T> List<T> read(final Class<T> returnType,final String query,final Map<String,Object> params ){
    	return this.read(returnType, query,params,null,null);
    }
    @SuppressWarnings("unchecked")
	public <T> List<T> read(final Class<T> returnType,final String query,final Map<String,Object> params,final Integer firstResult,final Integer limit ){
        List<T> list = null;
        try(Session session = this.getCreateSession()) {
        	Query queryO = session.createQuery(query);
        	var keys = params.keySet();
        	for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				String string = iterator.next();
				queryO.setParameter(string, params.get(string));	
			}
        	if((firstResult != null) && (limit != null)){
        		list = queryO.setFirstResult(firstResult).setMaxResults(limit).list();	
        	} else {
        		list = queryO.list();	
        	}
        } catch (HibernateException e) {
        	logger.error(e);
        }
        return list;
    }
}