package com.rogy.smarte.repository.db1;

import com.rogy.smarte.entity.db1.SwitchParamSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwitchParamSettingDao extends JpaRepository<SwitchParamSetting, String> {

//    @PersistenceContext
//    private EntityManager entityManager;
//
//    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Exception.class})
//    public SwitchParamSetting addOrUpdateSwitchParamSetting(SwitchParamSetting switchParamSetting) throws Exception {
//        return entityManager.merge(switchParamSetting);
//    }

}
