package io.terminus.dalaran.repository.release;

import io.terminus.dalaran.entity.release.SubFlowReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SubFlowReleasedRepository extends JpaRepository<SubFlowReleasedEntity, Long>, JpaSpecificationExecutor<SubFlowReleasedEntity> {
    List<SubFlowReleasedEntity> findByVersion(String version);
}
