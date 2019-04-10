package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.entity.FlowEntity;
import io.terminus.dalaran.console.model.query.FlowQuery;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface FlowQueryService {

    List<FlowEntity> query(FlowQuery query);

    List<FlowEntity> queryByProcessorIds(List<Long> processorIds);
}
