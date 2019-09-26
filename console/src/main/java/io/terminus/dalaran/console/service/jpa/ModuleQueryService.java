package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.core.resource.entity.common.ModuleEntity;
import io.terminus.dalaran.model.query.ModuleQuery;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface ModuleQueryService {

    List<ModuleEntity> query(ModuleQuery query);
}
