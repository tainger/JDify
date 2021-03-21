package io.terminus.dalaran.core.resource.repository;


import io.terminus.dalaran.core.resource.entity.common.PrivateRepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PrivateRepositoryRepository extends JpaRepository<PrivateRepositoryEntity, Long>, JpaSpecificationExecutor<PrivateRepositoryEntity> {

    PrivateRepositoryEntity findByResourceKeyAndVersion(String resourceKey, String version);

    PrivateRepositoryEntity findByNameAndType(String name, String type);

}
