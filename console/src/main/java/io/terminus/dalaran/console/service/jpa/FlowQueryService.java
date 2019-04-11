package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.console.entity.FlowEntity;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.console.model.query.rst.ComponentInfo;
import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface FlowQueryService {

    List<FlowEntity> query(FlowQuery query);

    List<FlowEntity> queryByProcessorIds(List<Long> processorIds);

    List<ComponentInfo> getBasicInfo(Long moduleId);
}
