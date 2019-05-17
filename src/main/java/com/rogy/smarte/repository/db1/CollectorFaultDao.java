package com.rogy.smarte.repository.db1;/*package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import model.CollectorFault;

import org.springframework.stereotype.Repository;

@Repository
public class CollectorFaultDao {

	@PersistenceContext
	private EntityManager entityManager;

	public CollectorFault addOrUpdateCollectorFault(CollectorFault f) throws Exception {
		CollectorFault flt = entityManager.merge(f);
		return flt;
	}

	
	public void deleteCollectorFault(CollectorFault f) throws Exception {
		entityManager.remove(f);
	}
	
	*//**
	 * 根据记录id查找Fault对象
	 * @param id 记录id
	 * @return 对应的对象
	 * @throws Exception
	 *//*
	public CollectorFault findById(String id) throws Exception {
		if (id == null || id.isEmpty())
			return null;
		TypedQuery<CollectorFault> query = entityManager.createNamedQuery(
				"CollectorFault.findById", CollectorFault.class);
		query.setParameter("id", id);
		List<CollectorFault> result = query.getResultList();
		if (result == null || result.isEmpty())
			return null;
		return result.get(0);
	}
	
	*//**
	 * 根据Collector id查找Fault对象
	 * @param collectorID 记录id
	 * @return 对应的对象
	 * @throws Exception
	 *//*
	public CollectorFault findByCollectorId(String collectorID) throws Exception {
		if (collectorID == null || collectorID.isEmpty())
			return null;
		TypedQuery<CollectorFault> query = entityManager.createNamedQuery(
				"CollectorFault.findByCollectorId", CollectorFault.class);
		query.setParameter("collectorID", collectorID);
		List<CollectorFault> result = query.getResultList();
		if (result == null || result.isEmpty())
			return null;
		return result.get(0);
	}
	

	public List<CollectorFault> findAll() throws Exception {
		TypedQuery<CollectorFault> query = entityManager.createNamedQuery(
				"CollectorFault.findAll", CollectorFault.class);
		List<CollectorFault> result = query.getResultList();
		return result;
	}
}
*/