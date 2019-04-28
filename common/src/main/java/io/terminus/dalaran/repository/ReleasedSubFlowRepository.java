package io.terminus.dalaran.repository;

import io.terminus.dalaran.entity.release.ReleasedSubFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ReleasedSubFlowRepository extends JpaRepository<ReleasedSubFlowEntity, Long>, JpaSpecificationExecutor<ReleasedSubFlowEntity> {
    List<ReleasedSubFlowEntity> findByVersion(String version);
}
