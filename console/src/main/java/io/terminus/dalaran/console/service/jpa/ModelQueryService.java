package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.model.dto.basic.BasicModelInfo;
import io.terminus.dalaran.model.query.ModelQuery;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface ModelQueryService {

    List<ModelEntity> query(ModelQuery query);

    List<BasicModelInfo> listBasicInfoByModuleId(Long moduleId);
}
