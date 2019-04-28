package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.BodyType;
import io.terminus.dalaran.console.model.query.ModelQuery;
import io.terminus.dalaran.console.model.query.rst.ComponentInfo;
import io.terminus.dalaran.entity.ModelEntity;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface ModelQueryService {

    List<ModelEntity> query(ModelQuery query);

    List<BodyType> getTypes(Long moduleId);

    List<ComponentInfo> getBasicInfo(BodyType type);
}
