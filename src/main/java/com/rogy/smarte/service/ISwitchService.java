package com.rogy.smarte.service;

import com.rogy.smarte.entity.db1.Switch;

public interface ISwitchService {
    Switch addOrUpdate(Switch swich);

    boolean delete(String ids);
}
