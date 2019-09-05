package io.terminus.dalaran.console.model.dto;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
public class ImportProcessorDTO extends ImportInfo {

    @NotNull
    private String processorType;

    @Nullable
    private Map<String, Object> processorConfig;
}
