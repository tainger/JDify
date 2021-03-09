package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.LimiterDTO;
import io.terminus.dalaran.model.dto.basic.BasicLimiterInfo;

import java.util.List;

public interface LimiterService {

    String create(LimiterDTO limiterDTO);

    LimiterDTO update(LimiterDTO limiterDTO);

    void delete(String limiterId);

    LimiterDTO detail(String limiterId);

    List<BasicLimiterInfo> listBasicInfoByModuleId(String moduleId);

    List<BasicLimiterInfo> listBasicInfoByComponent(String limiterType);
}
