package io.terminus.dalaran.repository.specification;

import io.terminus.dalaran.entity.PropertyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by jingdi on 2019/4/16
 */
public interface PropertyQueryRepository extends JpaRepository<PropertyEntity, Long>, JpaSpecificationExecutor<PropertyEntity> {
}
