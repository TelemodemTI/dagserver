package main.cl.dagserver.infra.adapters.confs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class DAO {

	
	@PersistenceContext()
	private EntityManager entityManager;

	
	@Transactional
    public <T> T save(T o){
		entityManager.merge(o);
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
    	var list = queryO.getResultList();
    	
    	if(isListOfType(list, returnType)) {
    		return list;	
    	} else {
    		throw new IllegalArgumentException("not valid type"); 
    	}
    }
    private static <T> boolean isListOfType(List<?> list, Class<T> type) {
        if (list == null || type == null) {
            return false;
        }

        for (Object obj : list) {
            if (!type.isInstance(obj)) {
                return false;
            }
        }

        return true;
    }
}
