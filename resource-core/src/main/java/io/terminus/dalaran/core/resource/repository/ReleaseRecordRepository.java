package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.common.ReleaseRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReleaseRecordRepository extends JpaRepository<ReleaseRecordEntity, Long>, JpaSpecificationExecutor<ReleaseRecordEntity> {

    ReleaseRecordEntity findByEnabledTrue();

    ReleaseRecordEntity findByVersion(String version);
}
