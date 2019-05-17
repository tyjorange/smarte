package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;


@Repository
public interface RegionDao extends JpaRepository<Region, String> {

//	@PersistenceContext
//	private EntityManager entityManager;

//    public Region addOrUpdateRegion(Region region) throws Exception {
//        Region re = entityManager.merge(region);
//        return re;
//    }

//    public void deleteRegion(Region rn) throws Exception {
//        entityManager.remove(rn);
//    }

//    public List<Region> findRegion(RegionExp exp, Integer start, Integer length) throws Exception {
//        String sql = "select r.* from Region r where 1 = 1 ";
//        if (exp != null) {
//            if (exp.getRegionCode() != null && !exp.getRegionCode().isEmpty()) {
//                sql += " and r.regioncode like '%" + exp.getRegionCode() + "%' ";
//            }
//            if (exp.getRegionName() != null && !exp.getRegionName().isEmpty()) {
//                sql += " and r.regionname like '%" + exp.getRegionName() + "%' ";
//            }
//        }
//        sql += "order by r.regioncode asc ";
//        if (start != null && start >= 0 && length != null && length > 0)
//            sql += "limit " + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, Region.class);
//        List<Region> result = query.getResultList();
//        return result;
//    }

//    public int findRegionCount(RegionExp exp) throws Exception {
//        String sql = "select count(*) from Region r where 1 = 1 ";
//        if (exp != null) {
//            if (exp.getRegionCode() != null && !exp.getRegionCode().isEmpty()) {
//                sql += " and r.regioncode like '%" + exp.getRegionCode() + "%' ";
//            }
//            if (exp.getRegionName() != null && !exp.getRegionName().isEmpty()) {
//                sql += " and r.regionname like '%" + exp.getRegionName() + "%' ";
//            }
//        }
//        Query query = entityManager.createNativeQuery(sql);
//        BigInteger count = (BigInteger) query.getSingleResult();
//        return count.intValue();
//    }


    /**
     * 根据编码查询区域
     *
     * @param regionCode
     * @return
     * @throws Exception
     */
    @Query("SELECT r FROM Region r WHERE r.regionCode = :regionCode")
    List<Region> findRegionByCode(String regionCode);


    /**
     * 根据id查询区域
     *
     * @param regionID
     * @return
     * @throws Exception
     */
    @Query("SELECT r FROM Region r where r.regionID = :regionID")
    List<Region> findByIds(String regionID);


}
