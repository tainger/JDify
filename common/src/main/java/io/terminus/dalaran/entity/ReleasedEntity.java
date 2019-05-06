package io.terminus.dalaran.entity;

public interface ReleasedEntity {

    void setId(Long id);

    void setOriginId(Long originId);

    Long getOriginId();

    void setVersion(String version);

    String getVersion();
}
