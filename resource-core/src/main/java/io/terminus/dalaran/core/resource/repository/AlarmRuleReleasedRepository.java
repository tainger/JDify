package io.terminus.dalaran.core.resource.repository;

import io.terminus.dalaran.core.resource.entity.released.AlarmRuleReleasedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AlarmRuleReleasedRepository extends JpaRepository<AlarmRuleReleasedEntity, Long>, JpaSpecificationExecutor<AlarmRuleReleasedEntity> {
    AlarmRuleReleasedEntity findByVersionAndOriginId(String version, Long alarmRuleId);


    List<AlarmRuleReleasedEntity> findByVersion(String version);
}
