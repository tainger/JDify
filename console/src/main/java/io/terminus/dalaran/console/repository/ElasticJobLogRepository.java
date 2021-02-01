package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.JobExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ElasticJobLogRepository extends JpaRepository<JobExecutionEntity, Long>, JpaSpecificationExecutor<JobExecutionEntity> {

}
