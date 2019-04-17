package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.console.model.query.TriggerQuery;
import io.terminus.dalaran.console.model.query.rst.ComponentInfo;
import io.terminus.dalaran.console.model.query.rst.ComponentType;
import io.terminus.dalaran.entity.TriggerEntity;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface TriggerQueryService {

    List<TriggerEntity> query(TriggerQuery query);

    List<ComponentType> getTypes(Long moduleId);

    List<ComponentInfo> getBasicInfo(String type);
}
