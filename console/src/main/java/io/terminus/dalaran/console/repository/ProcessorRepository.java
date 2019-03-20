package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.ProcessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessorRepository extends JpaRepository<ProcessorEntity, Long> {
}
