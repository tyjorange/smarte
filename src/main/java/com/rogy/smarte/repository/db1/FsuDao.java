package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.Fsu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

@Repository
public interface FsuDao extends JpaRepository<Fsu, String> {

//    @PersistenceContext
//    private EntityManager entityManager;

//    public Fsu addOrUpdateFsu(Fsu fsu) throws Exception {
//        Fsu fs = entityManager.merge(fsu);
//        return fs;
//    }
//
//
//    public void deleteFsu(Fsu su) throws Exception {
//        entityManager.remove(su);
//    }


    /**
     * 根据ID查询FSU
     *
     * @param fsuid
     * @return
     * @throws Exception
     */
    @Query("SELECT f FROM Fsu f where f.fsuid = :fsuid")
    List<Fsu> findByIds(String fsuid);


    /**
     * 根据编码查询Fsu
     *
     * @param FSUCode
     * @return
     * @throws Exception
     */
    @Query("SELECT f FROM Fsu f WHERE f.FSUCode = :FSUCode")
    List<Fsu> findFsuByCode(String FSUCode);


//    public List<Fsu> findFsu(FsuExp exp, Integer start, Integer length) throws Exception {
//        String sql = "select f.* from Fsu f inner join Subregion s on f.SubRegionID = s.SubRegionID";
//        if (exp != null) {
//            if (exp.getFsucode() != null && !exp.getFsucode().isEmpty()) {
//                sql += " and f.FSUCode like '%" + exp.getFsucode() + "%' ";
//            }
//            if (exp.getFsuname() != null && !exp.getFsuname().isEmpty()) {
//                sql += " and f.FSUName like '%" + exp.getFsuname() + "%' ";
//            }
//            if (exp.getSubregionid() != null && !exp.getSubregionid().isEmpty()) {
//                sql += " and s.SubRegionID = '" + exp.getSubregionid() + "' ";
//            }
//
//        }
//        sql += " order by f.FSUCode asc ";
//        if (start != null && start >= 0 && length != null && length > 0)
//            sql += "limit " + start + ", " + length;
//        Query query = entityManager.createNativeQuery(sql, Fsu.class);
//        List<Fsu> result = query.getResultList();
//        return result;
//    }

//    public int findFsuCount(FsuExp exp) throws Exception {
//        String sql = "select  count(*)  from Fsu f inner join Subregion s on f.SubRegionID = s.SubRegionID";
//        if (exp != null) {
//            if (exp.getFsucode() != null && !exp.getFsucode().isEmpty()) {
//                sql += " and f.FSUCode like '%" + exp.getFsucode() + "%' ";
//            }
//            if (exp.getFsuname() != null && !exp.getFsuname().isEmpty()) {
//                sql += " and f.FSUName like '%" + exp.getFsuname() + "%' ";
//            }
//            if (exp.getSubregionid() != null && !exp.getSubregionid().isEmpty()) {
//                sql += " and s.SubRegionID = '" + exp.getSubregionid() + "' ";
//            }
//        }
//        Query query = entityManager.createNativeQuery(sql);
//        BigInteger count = (BigInteger) query.getSingleResult();
//        return count.intValue();
//    }


    /**
     * 根据子区域查询Fsu
     *
     * @param subregionID
     * @return
     * @throws Exception
     */
    @Query("SELECT f FROM Fsu f where f.subregion.subRegionID = :subregionID")
    List<Fsu> findFsuBySubregionID(String subregionID);

}
