package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.ProcessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessorRepository extends JpaRepository<ProcessorEntity, Long> {
    
}
