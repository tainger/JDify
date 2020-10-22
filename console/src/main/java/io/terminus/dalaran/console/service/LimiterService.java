package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.LimiterDTO;
import io.terminus.dalaran.model.dto.basic.BasicLimiterInfo;

import java.util.List;

public interface LimiterService {

    Long create(LimiterDTO limiterDTO);

    LimiterDTO update(LimiterDTO limiterDTO);

    void delete(Long limiterId);

    LimiterDTO detail(Long limiterId);

    List<BasicLimiterInfo> listBasicInfoByModuleId(Long moduleId);

    List<BasicLimiterInfo> listBasicInfoByComponent(String limiterType);
}
