package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by jingdi on 2019/4/1
 */
public interface ModuleRepository extends JpaRepository<ModuleEntity, Long> {
}
