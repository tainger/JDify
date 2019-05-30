package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.console.entity.PropertyEntity;
import io.terminus.dalaran.console.model.query.PropertyQuery;

import java.util.List;

/**
 * Created by jingdi on 2019/4/16
 */

public interface PropertyQueryService {

    List<PropertyEntity> query(PropertyQuery query);
}
