package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.ConnectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ConnectorRepository extends JpaRepository<ConnectorEntity, Long>, JpaSpecificationExecutor<ConnectorEntity> {

    List<ConnectorEntity> findByIsExistTrue();

    List<ConnectorEntity> findByModuleIdAndIsExistTrue(Long moduleId);

}
