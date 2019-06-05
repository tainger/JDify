package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.ConnectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConnectorRepository extends JpaRepository<ConnectorEntity, Long>, JpaSpecificationExecutor<ConnectorEntity> {
}
