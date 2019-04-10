package io.terminus.dalaran.repository;

import io.terminus.dalaran.entity.ProcessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessorRepository extends JpaRepository<ProcessorEntity, Long> {
    
}
