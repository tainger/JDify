package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.TracingType;
import io.terminus.dalaran.core.resource.entity.common.TracingLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface TracingLogRepository extends JpaRepository<TracingLogEntity, Long>, JpaSpecificationExecutor<TracingLogEntity> {

    List<TracingLogEntity> findByFlowIdAndTracingType(Long flowId, TracingType tracingType);

    List<TracingLogEntity> findByRecordIdAndTracingType(String recordId, TracingType tracingType);

    TracingLogEntity findOneByRecordIdAndTracingType(String recordId, TracingType tracingType);

    TracingLogEntity findByRecordIdAndMainTrue(String recordId);

    void deleteByCreatedAtBefore(Date datetime);

    List<TracingLogEntity> findByFlowIdAndVersion(Long flowId, String version);

    List<TracingLogEntity> findByFlowIdAndVersionAndSuccessful(Long flowId, String version, boolean successful);
}