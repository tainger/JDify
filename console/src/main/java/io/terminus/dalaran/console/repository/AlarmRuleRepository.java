package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.AlarmRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AlarmRuleRepository extends JpaRepository<AlarmRuleEntity, Long>, JpaSpecificationExecutor<AlarmRuleEntity> {
    List<AlarmRuleEntity> findByIsExistTrue();
}
