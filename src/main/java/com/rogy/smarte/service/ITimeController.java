package com.rogy.smarte.service;

import java.sql.Time;
import java.util.List;

import javax.persistence.EntityManager;

import com.rogy.smarte.entity.db1.Switch;
import com.rogy.smarte.entity.db1.TimeController;

public interface ITimeController {
	TimeController findById(Long id);
	TimeController add(final Switch swt, byte cmdData, Time runTime, byte state, int weekday, byte upload);
	void deleteById(Long id);
	void deleteByIds(String ids);
	TimeController updateById(Long id, Byte cmdData, Time runTime, Byte state, Integer weekday, Byte upload);
	List<TimeController> findByIDList(String list);
}
