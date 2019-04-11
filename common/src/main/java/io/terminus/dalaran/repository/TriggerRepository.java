package io.terminus.dalaran.repository;

import io.terminus.dalaran.entity.TriggerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jingdi on 2019/3/29
 */
public interface TriggerRepository extends JpaRepository<TriggerEntity, Long> {

    TriggerEntity findByFlowId(Long flowId);
}
