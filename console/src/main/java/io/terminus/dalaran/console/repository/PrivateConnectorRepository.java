package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.PrivateConnectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PrivateConnectorRepository extends JpaRepository<PrivateConnectorEntity, Long>, JpaSpecificationExecutor<PrivateConnectorEntity> {

    List<PrivateConnectorEntity> findByResourceKey(String resourceKey);
}
