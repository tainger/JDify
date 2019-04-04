package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.TriggerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TriggerRepository extends JpaRepository<TriggerEntity, Long> {
}
