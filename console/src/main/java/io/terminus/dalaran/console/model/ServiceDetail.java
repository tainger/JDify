package io.terminus.dalaran.console.model;

import io.terminus.dalaran.console.entity.ServiceEntity;
import io.terminus.dalaran.core.component.DalaranService;
import lombok.Data;

/**
 * Created by jingdi on 2019/7/30
 */
@Data
public class ServiceDetail {

    private ServiceEntity entity;

    private Object serviceConfig;

    private Object importConfig;

    private DalaranService dalaranService;

    private Long moduleId;

    private Long serviceId;
}
