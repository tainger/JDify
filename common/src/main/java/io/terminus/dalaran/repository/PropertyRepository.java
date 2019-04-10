package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<PropertyEntity, Long> {
}
