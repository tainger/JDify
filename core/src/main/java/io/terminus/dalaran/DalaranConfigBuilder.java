package io.terminus.dalaran;

public interface DalaranConfigBuilder {

    <T> T buildTriggerConfig(String configJson, String type);

    <T> T buildProcessorConfig(String config, String type);

    <T> T buildModelSchema(String modelSchema, BodyType type);

    <T> T buildConnectorConfig(String config, ComponentType componentType);

    <T> T buildServiceConfig(String serviceConfig, String type);
}
