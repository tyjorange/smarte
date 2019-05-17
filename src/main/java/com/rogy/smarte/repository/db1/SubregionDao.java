package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.Subregion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubregionDao extends JpaRepository<Subregion, String> {

//	@PersistenceContext
//	private EntityManager entityManager;

//    public Subregion addOrUpdateSubregion(Subregion subregion) throws Exception {
//        Subregion sr = entityManager.merge(subregion);
//        return sr;
//    }

//    public void deleteSubregion(Subregion sr) throws Exception {
//        entityManager.remove(sr);
//    }


//    public List<Subregion> findSubregion(SubregionExp exp, Integer start, Integer length) throws Exception {
//        String sql = "select s.* from Subregion s inner join region r on s.regionID = r.regionID";
//        if (exp != null) {
//            if (exp.getSubregioncode() != null && !exp.getSubregioncode().isEmpty()) {
//                sql += " and s.subregionCode like '%" + exp.getSubregioncode() + "%' ";
//            }
//            if (exp.getSubregionname() != null && !exp.getSubregionname().isEmpty()) {
//                sql += " and s.subregionName like '%" + exp.getSubregionname() + "%' ";
//            }
//            if (exp.getRegionid() != null && !exp.getRegionid().isEmpty()) {
//                sql += " and r.regionID = '" + exp.getRegionid() + "' ";
//            }
//
//        }
//        sql += " order by s.subregionCode asc ";
//        if (start != null && start >= 0 && length != null && length > 0)
//            sql += "limit " + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, Subregion.class);
//        List<Subregion> result = query.getResultList();
//        return result;
//    }

//    public int findSubregionCount(SubregionExp exp) throws Exception {
//        String sql = "select count(*) from Subregion s inner join region r on s.regionID = r.regionID";
//        if (exp != null) {
//            if (exp.getSubregioncode() != null && !exp.getSubregioncode().isEmpty()) {
//                sql += " and s.subregionCode like '%" + exp.getSubregioncode() + "%' ";
//            }
//            if (exp.getSubregionname() != null && !exp.getSubregionname().isEmpty()) {
//                sql += " and s.subregionName like '%" + exp.getSubregionname() + "%' ";
//            }
//            if (exp.getRegionid() != null && !exp.getRegionid().isEmpty()) {
//                sql += " and r.regionID = '" + exp.getRegionid() + "' ";
//            }
//
//        }
//        Query query = entityManager.createNativeQuery(sql);
//        BigInteger count = (BigInteger) query.getSingleResult();
//        return count.intValue();
//    }


    /**
     * 根据编码查询区域
     *
     * @param subRegionCode
     * @return
     * @throws Exception
     */
    @Query("SELECT s FROM Subregion s WHERE s.subRegionCode = :subRegionCode")
    List<Subregion> findSubregionByCode(String subRegionCode);

    /**
     * 根据编码查询区域
     *
     * @param regionID
     * @return
     * @throws Exception
     */
    @Query("SELECT s FROM Subregion s WHERE s.region.regionID = :regionID")
    List<Subregion> findSubregionByRegionID(String regionID);

    /**
     * 根据id查询区域
     *
     * @param subRegionID
     * @return
     * @throws Exception
     */
    @Query("SELECT s FROM Subregion s where s.subRegionID = :subRegionID")
    List<Subregion> findByIds(String subRegionID);

}
