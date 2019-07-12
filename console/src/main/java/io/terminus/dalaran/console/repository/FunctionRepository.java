package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.FunctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FunctionRepository extends JpaRepository<FunctionEntity, Long>, JpaSpecificationExecutor<FunctionEntity> {
}
