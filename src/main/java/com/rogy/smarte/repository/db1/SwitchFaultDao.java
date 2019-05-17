package com.rogy.smarte.repository.db1;/*package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import model.SwitchFault;

import org.springframework.stereotype.Repository;

@Repository
public class SwitchFaultDao {

	@PersistenceContext
	private EntityManager entityManager;

	public SwitchFault addOrUpdateSwitchFault(SwitchFault f) throws Exception {
		SwitchFault flt = entityManager.merge(f);
		return flt;
	}

	
	public void deleteSwitchFault(SwitchFault f) throws Exception {
		entityManager.remove(f);
	}
	
	*//**
	 * 根据记录id查找Fault对象
	 * @param id 记录id
	 * @return 对应的对象
	 * @throws Exception
	 *//*
	public SwitchFault findById(String id) throws Exception {
		if (id == null || id.isEmpty())
			return null;
		TypedQuery<SwitchFault> query = entityManager.createNamedQuery(
				"SwitchFault.findById", SwitchFault.class);
		query.setParameter("id", id);
		List<SwitchFault> result = query.getResultList();
		if (result == null || result.isEmpty())
			return null;
		return result.get(0);
	}
	
	*//**
	 * 根据Switch id查找Fault对象
	 * @param switchID 记录id
	 * @return 对应的对象
	 * @throws Exception
	 *//*
	public SwitchFault findBySwitchId(String switchID) throws Exception {
		if (switchID == null || switchID.isEmpty())
			return null;
		TypedQuery<SwitchFault> query = entityManager.createNamedQuery(
				"SwitchFault.findBySwitchId", SwitchFault.class);
		query.setParameter("switchID", switchID);
		List<SwitchFault> result = query.getResultList();
		if (result == null || result.isEmpty())
			return null;
		return result.get(0);
	}
	
	
	public List<SwitchFault> findAll() throws Exception {
		TypedQuery<SwitchFault> query = entityManager.createNamedQuery(
				"SwitchFault.findAll", SwitchFault.class);
		List<SwitchFault> result = query.getResultList();
		return result;
	}
}
*/