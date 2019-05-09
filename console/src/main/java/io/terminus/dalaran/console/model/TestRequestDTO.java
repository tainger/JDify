package io.terminus.dalaran.console.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class TestRequestDTO {

    @NotNull
    private Long flowId;

    @Nullable
    private String body;
}
