package main.cl.dagserver.infra.adapters.confs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DAO {

	@PersistenceContext()
	private EntityManager entityManager;
	
	@Transactional
    public <T> T save(T o){
		entityManager.merge(o);
		entityManager.flush();
		return o;
	}
	@Transactional
	public void delete(Object object){
		entityManager.remove(object);
	}
	@Transactional
	public void execute (final String query, Map<String,Object> params){
		Query queryO = entityManager.createNativeQuery(query);
		var keys = params.keySet();
    	for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			queryO.setParameter(string,params.get(string));	
		}
    	queryO.executeUpdate();
	}
	public <T> List<T> read(final Class<T> returnType,final String query ){
    	Map<String,Object> params = new HashMap<>();
    	return this.read(returnType, query,params,null,null);
    }
    public <T> List<T> read(final Class<T> returnType,final String query,final Map<String,Object> params ){
    	return this.read(returnType, query,params,null,null);
    }
    @SuppressWarnings("unchecked")
	public <T> List<T> read(final Class<T> returnType,final String query,final Map<String,Object> params,final Integer firstResult,final Integer limit ){
    	Query queryO = entityManager.createQuery(query);
    	var keys = params.keySet();
    	for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			queryO.setParameter(string,params.get(string));	
		}
    	if(firstResult!=null) {
    		queryO.setFirstResult(firstResult);	
    	}
    	if(limit!=null) {
    		queryO.setMaxResults(limit);	
    	}
    	return queryO.getResultList();
    }
}
