package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.log.TracingType;
import io.terminus.dalaran.core.resource.entity.common.TracingLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TracingLogRepository extends JpaRepository<TracingLogEntity, Long>, JpaSpecificationExecutor<TracingLogEntity> {

    List<TracingLogEntity> findByFlowIdAndTracingType(Long flowId, TracingType tracingType);

    List<TracingLogEntity> findByRecordIdAndTracingType(String recordId, TracingType tracingType);

    TracingLogEntity findOneByRecordIdAndTracingType(String recordId, TracingType tracingType);

    TracingLogEntity findByRecordIdAndMainTrue(String recordId);
}