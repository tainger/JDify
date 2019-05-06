package io.terminus.dalaran.repository;

import io.terminus.dalaran.entity.ConnectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConnectorRepository extends JpaRepository<ConnectorEntity, Long>, JpaSpecificationExecutor<ConnectorEntity> {
}
