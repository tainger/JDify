package io.terminus.dalaran.core.flow.model;

import io.terminus.dalaran.core.DalaranConstants;
import io.terminus.dalaran.core.component.model.ProcessorModel;
import io.terminus.dalaran.core.flow.FlowStatus;
import io.terminus.dalaran.core.model.MessageModel;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.terminus.dalaran.core.DalaranConstants.DIRECT_PREFIX;

@Data
public class BasicFlow {

    @NotNull
    private Long id;

    private String version;

    private FlowStatus status;

    @Nullable
    private MessageModel inModel;

    @Nullable
    private MessageModel outModel;

    @NotNull
    private List<ProcessorModel> pipeline;

    private boolean tracing;

    public String getRouteId() {
        return DalaranConstants.FLOW_PREFIX + this.getId();
    }

    public String getDirectRouteUri() {
        return DIRECT_PREFIX + this.getRouteId();
    }
}
