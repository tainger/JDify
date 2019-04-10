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

    @Nullable
    private Boolean retryable;

    @Nullable
    private Integer maxRetry;

    @Nullable
    private Integer retryDelay;

    private MessageModel inModel;

    private MessageModel outModel;

    @NotNull
    private List<ProcessorModel> processors;

    @NotNull
    private Map<String, String> properties;


}
