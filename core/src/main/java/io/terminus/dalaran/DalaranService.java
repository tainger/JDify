package io.terminus.dalaran;

public interface DalaranService<ImportConfig, ServiceConfig, ProcessorConfig, ServiceSelectConfig> {



    ServiceConfig importConfig(ImportConfig config);

    ProcessorConfig configure(ServiceConfig serviceConfig, ServiceSelectConfig selectConfig);
}
