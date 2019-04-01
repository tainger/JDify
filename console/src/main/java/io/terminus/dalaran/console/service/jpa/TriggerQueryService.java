package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.console.entity.TriggerEntity;
import io.terminus.dalaran.console.model.query.TriggerQuery;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface TriggerQueryService {

    List<TriggerEntity> query(TriggerQuery query);
}
