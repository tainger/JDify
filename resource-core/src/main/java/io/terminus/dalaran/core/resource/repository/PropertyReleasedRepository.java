package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.released.PropertyReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PropertyReleasedRepository extends JpaRepository<PropertyReleasedEntity, Long>, JpaSpecificationExecutor<PropertyReleasedEntity> {
    List<PropertyReleasedEntity> findByVersion(String version);
}
