package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PrivateResourceQueryService {

    List<PrivateRepositoryEntity> query(PrivateRepositoryQuery query);

    Page<PrivateRepositoryEntity> paging(PrivateRepositoryQuery query, Integer pageNumber, Integer pageSize);

    List<String> listResourceVersion(String id);

    List<PrivateRepositoryEntity> findByResourceKeyAndVersion(String resourceKey, String version);

    List<PrivateRepositoryEntity> listPackageResource();
}
