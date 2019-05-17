package com.rogy.smarte.service;

import com.rogy.smarte.entity.db1.Collector;

public interface ICollectorService {
    int updateCollector(Collector c) throws Exception;

    Collector addOrUpdate(Collector collector) throws Exception;

    boolean delete(String ids) throws Exception;
}
