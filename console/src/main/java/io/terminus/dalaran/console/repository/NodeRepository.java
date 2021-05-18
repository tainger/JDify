package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.NodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NodeRepository extends JpaRepository<NodeEntity, Long>, JpaSpecificationExecutor<NodeEntity> {

    NodeEntity findByResourceKey(String resourceKey);

}
