package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;

import java.util.List;

public interface PrivateResourceQueryService {

    List<PrivateRepositoryEntity> query(PrivateRepositoryQuery query);

    List<String> listResourceVersion(String id);
}
