package io.terminus.dalaran.model.flow;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.terminus.dalaran.DalaranConstants.FLOW_PREFIX;

@Data
public class TriggerFlow extends BasicFlow {

    @NotNull
    private String triggerType;

    @Nullable
    private Object triggerConfig;

    @Override
    public String getRouteId() {
        return FLOW_PREFIX + this.getId();
    }
}
