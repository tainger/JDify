package io.terminus.dalaran.core.flow.model;

import io.terminus.dalaran.core.DalaranConstants;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public class TriggerFlow extends BasicFlow {

    @NotNull
    private String triggerType;

    @Nullable
    private Object triggerConfig;

    @Override
    public String getRouteId() {
        return DalaranConstants.FLOW_PREFIX + this.getId();
    }
}
