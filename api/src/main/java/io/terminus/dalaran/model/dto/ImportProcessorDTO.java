package io.terminus.dalaran.model.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
public class ImportProcessorDTO extends ImportInfo {

    @NotNull
    private String processorType;

    private String processorGroup;

    private String processorVersion;

    @Nullable
    private Map<String, Object> processorConfig;
}
