package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.TriggerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jingdi on 2019/3/29
 */
public interface TriggerRepository extends JpaRepository<TriggerEntity, Long> {

    TriggerEntity findByFlowId(Long flowId);
}
