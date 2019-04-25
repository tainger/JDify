package io.terminus.dalaran.repository;

import io.terminus.dalaran.entity.ProcessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProcessorRepository extends JpaRepository<ProcessorEntity, Long>, JpaSpecificationExecutor<ProcessorEntity> {
    
}
