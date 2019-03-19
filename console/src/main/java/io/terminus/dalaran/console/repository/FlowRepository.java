package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.FlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlowRepository extends JpaRepository<FlowEntity, Long> {
}
