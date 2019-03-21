package io.terminus.dalaran.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@Data
public class DalaranFlow {

    @NotNull
    private String id;

    @NotNull
    private Trigger trigger;

    @Nullable
    private Boolean retryable;

    @Nullable
    private Integer maxRetry;

    @Nullable
    private Integer retryDelay;

    @NotNull
    private List<Processor> processors;

    @NotNull
    private Map<String, String> properties;

    @Data
    public static class Trigger<T> {
        @NotNull
        private Long id;

        @NotNull
        private String type;

        @Nullable
        private T config;

        @Nullable
        private MessageModel inModel;

        @Nullable
        private MessageModel outModel;
    }

    @Data
    public static class Processor<T> {
        @NotNull
        private Long id;

        @NotNull
        private String type;

        @Nullable
        private T config;

        @Nullable
        private MessageModel inModel;

        @Nullable
        private MessageModel outModel;
    }
}
