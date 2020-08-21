package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.LimiterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LimiterRepository extends JpaRepository<LimiterEntity, Long>, JpaSpecificationExecutor<LimiterEntity> {
}
