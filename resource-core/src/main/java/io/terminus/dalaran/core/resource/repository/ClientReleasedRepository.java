package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.released.ClientReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ClientReleasedRepository extends JpaRepository<ClientReleasedEntity, Long>, JpaSpecificationExecutor<ClientReleasedEntity> {
    List<ClientReleasedEntity> findByVersion(String version);
}
