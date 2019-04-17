package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.console.model.query.StructureQuery;
import io.terminus.dalaran.console.model.query.rst.ComponentInfo;
import io.terminus.dalaran.console.model.query.rst.type.StructureType;
import io.terminus.dalaran.entity.StructureEntity;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface StructQueryService {

    List<StructureEntity> query(StructureQuery query);

    List<StructureType> getTypes(Long moduleId);

    List<ComponentInfo> getBasicInfo(BodyModelType type);
}
