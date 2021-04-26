package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.ClientEntity;
import io.terminus.dalaran.console.entity.TriggerFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.ArrayList;
import java.util.List;

public interface ClientRepository extends JpaRepository<ClientEntity, Long>, JpaSpecificationExecutor<ClientEntity> {

    List<ClientEntity> findByIsExistTrue();

    List<ClientEntity> findByModuleIdAndIsExistTrue(String moduleId);

    ClientEntity findByResourceKey(String resourceKey);

    ClientEntity findByAppKey(String appKey);

    List<ClientEntity> findByResourceKeyIn(List<String> resourceKeys);
}
