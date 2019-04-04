package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.console.entity.StructureEntity;
import io.terminus.dalaran.console.model.query.StructureQuery;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
public interface StructQueryService {

    List<StructureEntity> query(StructureQuery query);
}
