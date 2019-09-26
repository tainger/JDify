package io.terminus.dalaran.model.flow;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class TriggerFlow extends BasicFlow {

    @NotNull
    private String triggerType;

    @Nullable
    private Object triggerConfig;
}
