package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.dto.basic.BasicLimiterInfo;
import lombok.Data;

import java.util.Map;

@Data
public class LimiterDTO extends BasicLimiterInfo {

    private String description;

    private Map<String, Object> config;
}
