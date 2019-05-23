package io.terminus.dalaran;

import io.terminus.dalaran.config.ImmutableModelConfig;
import org.apache.camel.model.ProcessorDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DalaranService<ImportConfig, ServiceConfig, OperationConfig extends ImmutableModelConfig> {

    void configure(ProcessorDefinition route, OperationConfig operationConfig);

    OperationConfig getOperationConfig(ServiceConfig serviceConfig, @NotNull String operationKey);

    List<String> operations(ServiceConfig serviceConfig);

    ServiceConfig importConfig(ImportConfig config);
}
