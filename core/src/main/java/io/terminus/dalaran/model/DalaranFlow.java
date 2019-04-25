package io.terminus.dalaran.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Data
public class DalaranFlow {

    @NotNull
    private Long id;

    private String version;

    @NotNull
    private String triggerType;

    @Nullable
    private Object triggerConfig;

    private MessageModel inModel;

    private MessageModel outModel;

    @NotNull
    private Map<Long, ProcessorModel> processorMap;

    @NotNull
    private List<Long> processingPipeline;

    @NotNull
    private Map<String, String> properties;
}
