package io.terminus.dalaran.model;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class TestRequestDTO {

    @NotNull
    private String flowId;

    @Nullable
    private String body;
}
