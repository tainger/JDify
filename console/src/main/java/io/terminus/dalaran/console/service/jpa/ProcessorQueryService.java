package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.console.entity.ProcessorEntity;
import io.terminus.dalaran.console.model.query.ProcessorQuery;
import io.terminus.dalaran.console.model.query.rst.ComponentInfo;
import io.terminus.dalaran.console.model.query.rst.ComponentType;

import java.util.List;

/**
 * Created by jingdi on 2019/3/29
 */
public interface ProcessorQueryService {

    List<ProcessorEntity> query(ProcessorQuery query);

    List<ComponentType> getTypes(Long moduleId);

    List<ComponentInfo> getBasicInfo(String type);
}
