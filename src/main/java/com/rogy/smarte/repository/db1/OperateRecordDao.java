package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.OperateRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperateRecordDao extends JpaRepository<OperateRecord, String> {

//    @PersistenceContext
//    private EntityManager entityManager;

//    public OperateRecord addOrUpdateOperateRecord(OperateRecord or) {
//        OperateRecord operateRecord = entityManager.merge(or);
//        return operateRecord;
//    }
}
