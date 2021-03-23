package io.terminus.dalaran.console.repository;

import io.terminus.dalaran.console.entity.PrivatePackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PrivatePackageRepository extends JpaRepository<PrivatePackageEntity, Long>, JpaSpecificationExecutor<PrivatePackageEntity> {

    PrivatePackageEntity findByResourceKeyAndVersion(String resourceKey, String version);
}
