package main.infra.adapters.confs;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
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
    public void deleteBy (final String query){
    	Session session = null;
        try {
      	    session = sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
      	    session = sessionFactory.openSession();
        }
        try {
        	Query queryO = session.createQuery(query);
            queryO.executeUpdate();
        } catch (HibernateException e) {
        	e.printStackTrace();
        } finally {
        	if (session.isOpen()){
            	session.close();
            }
        }
    }
    public List<Object> raw(final String query){
    	return read(Object.class,query,null,null,true);
    }
    public <T> List<T> read(final Class<T> returnType,final String query ){
    	return this.read(returnType, query,null,null,false);
    }
    @SuppressWarnings("unchecked")
	public <T> List<T> read(final Class<T> returnType,final String query,final Integer firstResult,final Integer limit,boolean nativeExec ){
    	Session session = null;
        try {
      	    session = sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
      	    session = sessionFactory.openSession();
        }  
        List<T> list = null;
        try {
        	if((firstResult != null) && (limit != null)){
        		if(nativeExec) {
        			list = session.createSQLQuery(query).setFirstResult(firstResult).setMaxResults(limit).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE).list();
        		} else {
        			list = session.createQuery(query).setFirstResult(firstResult).setMaxResults(limit).list();	
        		}
        	} else {
        		if(nativeExec) {
        			list = session.createSQLQuery(query).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE).list();
        		} else {
        			list = session.createQuery(query).list();	
        		}
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
    public <T> Integer count(String query){
    	Session session = null;
        try {
      	    session = sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
      	    session = sessionFactory.openSession();
        }
        Integer res = null;
        try {
        	res = ((Long) session.createQuery("select count(*) " + query).iterate().next() ).intValue();
        } catch (HibernateException e) {
        	e.printStackTrace();
        } finally {
        	if (session.isOpen()){
            	session.close();
            }
        }
    	return res;
    }
    public <T> Integer countDistinct(String distinctStr,String query){
    	Session session = null;
        try {
      	    session = sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
      	    session = sessionFactory.openSession();
        }
        Integer res = null;
        try {
        	res = ((Long) session.createQuery("select count(distinct "+distinctStr+") " + query).iterate().next() ).intValue();
        } catch (HibernateException e) {
        	e.printStackTrace();
        } finally {
        	if (session.isOpen()){
            	session.close();
            }
        }
    	return res;
    }
}