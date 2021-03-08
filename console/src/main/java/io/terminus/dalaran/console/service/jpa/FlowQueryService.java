package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;
import io.terminus.dalaran.model.query.FlowQuery;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface FlowQueryService {

    List<TriggerFlowEntity> query(FlowQuery query);

    List<TriggerFlowEntity> queryByProcessorIds(List<String> processorIds);

    List<BasicFlowInfo> listBasicInfoByModuleId(String moduleId);
}
