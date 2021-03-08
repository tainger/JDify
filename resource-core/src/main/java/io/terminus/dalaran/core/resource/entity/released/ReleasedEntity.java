package io.terminus.dalaran.core.resource.entity.released;

public interface ReleasedEntity {

    void setId(Long id);

    void setOriginId(String originId);

    String getOriginId();

    void setVersion(String version);

    String getVersion();

    void setResourceKey(String resourceKey);

    String getResourceKey();
}
