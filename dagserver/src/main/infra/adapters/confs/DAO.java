package main.infra.adapters.confs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
public class DAO{
	@Autowired
	private SessionFactory sessionFactory;
	@SuppressWarnings("unchecked")
	@Transactional
    public <T> T save(T o){
    	Session session = null;
    	session = sessionFactory.openSession();
    	if(!session.contains(o)){
    		o = (T) session.merge(o);
    	}
    	Transaction tx = null;
        try {
        	tx = session.beginTransaction();
    		session.saveOrUpdate(o);
    		tx.commit();
        } catch (HibernateException ex) {
        	ex.printStackTrace();
        } finally {
        	if (session.isOpen()){
            	session.close();
            }
        }
        return o;
    }
    public <T> void delete(Object object){
    	Session session = null;
        session = sessionFactory.openSession();
        if(!session.contains(object)){
        	object = session.merge(object);
    	}
        Transaction tx = null;
        try {
			tx = session.beginTransaction();
			session.delete(object);
			tx.commit();
        } catch (HibernateException ex) {
        	ex.printStackTrace();
        } finally {
        	if (session.isOpen()){
            	session.close();
            }
        }
    }
    public void execute (final String query, Map<String,Object> params){
    	Session session = null;
        try {
      	    session = sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
      	    session = sessionFactory.openSession();
        }
        try {
        	Query queryO = session.createQuery(query);
        	var keys = params.keySet();
        	for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				String string = iterator.next();
				queryO.setParameter(string, params.get(string));	
			}
            queryO.executeUpdate();
        } catch (HibernateException e) {
        	e.printStackTrace();
        } finally {
        	if (session.isOpen()){
            	session.close();
            }
        }
    }
    public <T> List<T> read(final Class<T> returnType,final String query ){
    	return this.read(returnType, query,new HashMap<String,Object>(){{}},null,null);
    }
    public <T> List<T> read(final Class<T> returnType,final String query,final Map<String,Object> params ){
    	return this.read(returnType, query,params,null,null);
    }
    @SuppressWarnings("unchecked")
	public <T> List<T> read(final Class<T> returnType,final String query,final Map<String,Object> params,final Integer firstResult,final Integer limit ){
    	Session session = null;
        try {
      	    session = sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
      	    session = sessionFactory.openSession();
        }  
        List<T> list = null;
        try {
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
        	e.printStackTrace();
        } finally {
        	if (session.isOpen()){
            	session.close();
            }
        }
        return list;
    }
}