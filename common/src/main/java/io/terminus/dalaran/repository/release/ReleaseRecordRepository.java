package io.terminus.dalaran.repository.release;

import io.terminus.dalaran.entity.manage.ReleaseRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReleaseRecordRepository extends JpaRepository<ReleaseRecordEntity, Long>, JpaSpecificationExecutor<ReleaseRecordEntity> {

    ReleaseRecordEntity findByEnabledTrue();

    ReleaseRecordEntity findByVersion(String version);
}
