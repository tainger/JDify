package io.terminus.dalaran;

import io.terminus.dalaran.model.ServiceOperation;
import org.apache.camel.model.ProcessorDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DalaranService<ImportConfig, ServiceConfig, OperationConfig extends ServiceOperation> {

    void configure(ProcessorDefinition route, OperationConfig operationConfig);

    OperationConfig getOperationConfig(ServiceConfig serviceConfig, @NotNull String operationKey);

    List<String> operations(ServiceConfig serviceConfig);

    ServiceConfig importConfig(ImportConfig config);
}
