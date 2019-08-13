package io.terminus.dalaran.console.model.dto;

import io.terminus.dalaran.console.model.dto.basic.BasicServiceInfo;
import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/28
 */
@Data
public class ServiceDTO extends BasicServiceInfo {

    private Map<String, Object> importConfig;

    private Map<String, Object> serviceConfig;

    private String description;
}
