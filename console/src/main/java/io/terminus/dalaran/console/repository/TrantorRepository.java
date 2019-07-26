package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.TrantorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrantorRepository extends JpaRepository<TrantorEntity, Long>, JpaSpecificationExecutor<TrantorEntity> {

    TrantorEntity findByModuleKey(String key);

}
