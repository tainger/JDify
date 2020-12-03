package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ClientRepository extends JpaRepository<ClientEntity, Long>, JpaSpecificationExecutor<ClientEntity> {

    List<ClientEntity> findByIsExistTrue();

    List<ClientEntity> findByModuleIdAndIsExistTrue(Long moduleId);

}
