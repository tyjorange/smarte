package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.ElectricalEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectricalEquipmentDao extends JpaRepository<ElectricalEquipment, String> {

//    @PersistenceContext
//    private EntityManager entityManager;

    @Query("SELECT e FROM ElectricalEquipment e WHERE e.switchs.switchID = :switchID")
    List<ElectricalEquipment> findBySwitchID(Integer switchID);

//    public ElectricalEquipment addOrUpdateEE(
//            ElectricalEquipment electricalEquipment) throws Exception {
//        ElectricalEquipment ee = entityManager.merge(electricalEquipment);
//        return ee;
//    }

    @Query("SELECT e FROM ElectricalEquipment e WHERE e.id = :id")
    List<ElectricalEquipment> findByIds(String id);

//    public boolean deleteEE(String id) throws Exception {
//        if (id == null || id.trim().isEmpty())
//            return false;
//        ElectricalEquipment ee = findByID(id);
//        if (ee == null)
//            return false;
//        entityManager.remove(ee);
//        return true;
//    }
}
