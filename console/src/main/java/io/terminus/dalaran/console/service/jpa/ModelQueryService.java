package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.console.entity.ModelEntity;
import io.terminus.dalaran.console.model.dto.BasicModelInfo;
import io.terminus.dalaran.console.model.query.ModelQuery;
import io.terminus.dalaran.core.model.BodyType;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface ModelQueryService {

    List<ModelEntity> query(ModelQuery query);

    List<BodyType> getTypes(Long moduleId);

    List<BasicModelInfo> listBasicInfoByModuleId(Long moduleId);
}
