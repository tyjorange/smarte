package com.rogy.smarte.repository.db1;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.rogy.smarte.entity.db1.Automation;

import java.util.List;

@Repository
public interface AutomationDao extends JpaRepository<Automation, String>{
//	@PersistenceContext
//	private EntityManager entityManager;
	/**
	 * 新增或修改
	 * @param region
	 * @return
	 * @throws Exception
	 */
//	public Automation addOrUpdateAutomation(Automation atm) throws Exception {
//		Automation automation = entityManager.merge(atm);
//		return automation;
//	}
	
	/**
	 * 2017 12 25
	 * 通过id查找
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Query("SELECT a FROM Automation a WHERE a.automationID=:id")
	List<Automation> findByIds(String id);

//	public void deleteAutomation(Automation automation) throws Exception {
//		entityManager.remove(automation);
//	}
	
	/**
	 * 2017 12 25
	 * 按条件搜索
	 * @param bean
	 * @param start
	 * @param length
	 * @return
	 * @throws Exception
	 */
//	@SuppressWarnings("unchecked")
//	public List<Automation> findByCondition(AutomationBean bean, Integer start, Integer length) throws Exception {
//		StringBuilder sql=new StringBuilder("SELECT a.* "
//				+ " FROM automation a,switch s,collector c,fsu f,subregion sub,region r "
//				+ " where a.switchID=s.switchID "
//				+ " and s.collectorID=c.collectorID "
//				+ " and c.fsuID=f.fsuID "
//				+ " and f.SubRegionID=sub.SubRegionID "
//				+ " and sub.RegionID=r.RegionID ");		
//		if(bean!=null){
//			if(bean.getRegionID()!=null&&!bean.getRegionID().isEmpty()){
//				sql.append(" and r.RegionID='"+bean.getRegionID()+"'");
//			}else{
//				sql.append(" and r.RegionID=' '");
//			}
//			if(bean.getSubRegionID()!=null&&!bean.getSubRegionID().isEmpty()){
//				sql.append(" and sub.SubRegionID='"+bean.getSubRegionID()+"'");
//			}
//			if(bean.getFsuID()!=null&&!bean.getFsuID().isEmpty()){
//				sql.append(" and f.fsuID='"+bean.getFsuID()+"'");
//			}
//			if(bean.getCollectorID()!=null&&!bean.getCollectorID().isEmpty()){
//				sql.append(" and c.collectorID='"+bean.getCollectorID()+"'");
//			}
//			if(bean.getSwitchID()!=null&&!bean.getSwitchID().isEmpty()){
//				sql.append(" and s.switchID='"+bean.getSwitchID()+"'");
//			}
//		}
//		if (start != null && length != null && start >= 0 && length > 0)
//			sql.append(" limit " + start + ", " + length);
//		Query query = entityManager.createNativeQuery(sql.toString(),
//				Automation.class);
//		List<Automation> result = query.getResultList();
//		return result;
//	}
	
	/**
	 * 2017 12 25
	 * 查找计数
	 * @param bean
	 * @return
	 * @throws Exception
	 */
//	public int findByCondition(AutomationBean bean) throws Exception {
//		StringBuilder sql=new StringBuilder("SELECT count(*) "
//				+ " FROM automation a,switch s,collector c,fsu f,subregion sub,region r "
//				+ " where a.switchID=s.switchID "
//				+ " and s.collectorID=c.collectorID "
//				+ " and c.fsuID=f.fsuID "
//				+ " and f.SubRegionID=sub.SubRegionID "
//				+ " and sub.RegionID=r.RegionID ");		
//		if(bean!=null){
//			if(bean.getRegionID()!=null&&!bean.getRegionID().isEmpty()){
//				sql.append(" and r.RegionID='"+bean.getRegionID()+"'");
//			}else{
//				sql.append(" and r.RegionID=' '");
//			}
//			if(bean.getSubRegionID()!=null&&!bean.getSubRegionID().isEmpty()){
//				sql.append(" and sub.SubRegionID='"+bean.getSubRegionID()+"'");
//			}
//			if(bean.getFsuID()!=null&&!bean.getFsuID().isEmpty()){
//				sql.append(" and f.fsuID='"+bean.getFsuID()+"'");
//			}
//			if(bean.getCollectorID()!=null&&!bean.getCollectorID().isEmpty()){
//				sql.append(" and c.collectorID='"+bean.getCollectorID()+"'");
//			}
//			if(bean.getSwitchID()!=null&&!bean.getSwitchID().isEmpty()){
//				sql.append(" and s.switchID='"+bean.getSwitchID()+"'");
//			}
//		}
//		Query query = entityManager.createNativeQuery(sql.toString());
//		BigInteger count = (BigInteger) query.getSingleResult();
//		int countInt = count.intValue();
//		return countInt;
//	}
	
	/**
	 * 2017 12 27
	 * 通过ATMID查找Condition
	 * @param atmID
	 * @return
	 */
//	public List<Condition> findConByAtmID(String atmID){
//		List<Condition> conditions=null;
//		if(atmID==null||atmID.isEmpty()){
//			return conditions;
//		}else{
//		Query query=entityManager.createNamedQuery("Condition.findConByAtmID",Condition.class);
//		conditions=query.getResultList();
//		if(conditions==null||conditions.size()==0){
//			return null;
//		}
//		return conditions;
//		}		
//	}
	
	/**
	 * 2017 12 27
	 * 通过switch查找Automation
	 * @param switchID
	 * @return
	 * @throws Exception
	 */
	@Query("SELECT a FROM Automation a WHERE a.switchs.switchID=:switchID")
	List<Automation> findBySwitchId(String switchID);
	
	/**
	 * 2017 12 28
	 * 通过switchID a.name判断是否存在数据
	 * @param switchID
	 * @param name
	 * @return
	 */
//	public boolean CheckByIDName(String switchID, String name){
//		if (switchID == null || switchID.isEmpty()||name.trim() == null || name.trim().isEmpty()) {
//			 return false;
//			 }
//		String sql= String.format("SELECT a.* FROM automation a "
//				+ " WHERE a.switchID='%s' "
//				+ " and a.name='%s' ", switchID,name);
//		Query query =entityManager.createNativeQuery(sql,Automation.class);
//		List<Automation> conditions=query.getResultList();
//		int count=conditions.size();
//		if(count>0){
//	 		return true;
//	 	}else{
//	 		return false;
//	 	}
//	}
	
	
//	public List<Automation> trueOrFalse(String switchID, String name){
//		if (switchID == null || switchID.isEmpty()||name.trim() == null || name.trim().isEmpty()) {
//			 return null;
//			 }
//		String sql= String.format("SELECT a.* FROM automation a "
//				+ " WHERE a.switchID='%s' "
//				+ " and a.name='%s' ", switchID,name);
//		Query query =entityManager.createNativeQuery(sql,Automation.class);
//		List<Automation> conditions=query.getResultList();
//		return conditions;
//	}
}
