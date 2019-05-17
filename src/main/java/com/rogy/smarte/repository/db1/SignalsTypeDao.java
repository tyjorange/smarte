package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.Signalstype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface SignalsTypeDao extends JpaRepository<Signalstype, String> {

//	@PersistenceContext
//	private EntityManager entityManager;

    @Query("SELECT s FROM Signalstype s WHERE s.signalsTypeID = :signalsTypeID")
    List<Signalstype> findBySignalsTypeID(String signalsTypeID);

    @Query("SELECT s FROM Signalstype s ORDER BY s.typeCode ASC")
    List<Signalstype> findAlls();

    @Query("SELECT s FROM Signalstype s WHERE s.typeCode = :typeCode")
    List<Signalstype> findByCode(String typeCode);

//    public static Signalstype findByCode(EntityManager entityManager, String typeCode) {
//        if (typeCode == null || typeCode.trim().isEmpty())
//            return null;
//        TypedQuery<Signalstype> query = entityManager.createNamedQuery(
//                "Signalstype.findByCode", Signalstype.class);
//        query.setParameter("code", typeCode);
//        List<Signalstype> result = query.getResultList();
//        if (result == null || result.isEmpty())
//            return null;
//        return result.get(0);
//    }

    /**
     * 模糊查询信号类型名称
     *
     * @param typeName
     * @return
     * @throws Exception
     */
//    public List<Signalstype> findByName(String typeName, int start, int length) throws Exception {
//        List<Signalstype> list = new ArrayList<Signalstype>();
//        String sql = "select * from signalstype where typename like '%" + typeName + "%' limit " + start + "," + length;
//        Query query = entityManager.createNativeQuery(sql, Signalstype.class);
//        list = query.getResultList();
//        return list;
//    }

    /**
     * 模糊查询信号类型的个数
     *
     * @param typeName
     * @return
     * @throws Exception
     */
//    public int findByNameCount(String typeName) throws Exception {
//        List<Signalstype> list = new ArrayList<Signalstype>();
//        String sql = "select * from signalstype where typename like '%" + typeName + "%'";
//        Query query = entityManager.createNativeQuery(sql, Signalstype.class);
//        list = query.getResultList();
//        if (list == null) {
//            return 0;
//        }
//        return list.size();
//    }

    /**
     * 更新或者插入信号类型到数据库
     */
//    public void modifySignalsType(Signalstype st) throws Exception {
//        entityManager.merge(st);
//    }

    /**
     * 删除指定id的信号类型
     */
//    public void delete(Signalstype st) throws Exception {
//        entityManager.remove(st);
//    }

    /**
     * 分页查找对应的页的信号类型
     *
     * @param start
     * @param length
     * @return
     * @throws Exception
     */
//    public List<Signalstype> findAllForPage(int start, int length) throws Exception {
//        String sql = "select * from signalstype order by typeCode limit ?,?";
//        Query query = entityManager.createNativeQuery(sql, Signalstype.class);
//        query.setParameter(1, start);
//        query.setParameter(2, length);
//        List<Signalstype> result = query.getResultList();
//        return result;
//    }
}
