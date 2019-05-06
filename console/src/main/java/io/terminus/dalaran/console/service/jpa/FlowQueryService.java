package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.console.model.dto.flow.BasicFlowInfo;
import io.terminus.dalaran.console.model.query.FlowQuery;
import io.terminus.dalaran.entity.manage.TriggerFlowEntity;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface FlowQueryService {

    List<TriggerFlowEntity> query(FlowQuery query);

    List<TriggerFlowEntity> queryByProcessorIds(List<Long> processorIds);

    List<BasicFlowInfo> listBasicInfoByModuleId(Long moduleId);
}
