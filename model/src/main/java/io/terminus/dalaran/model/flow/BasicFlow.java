package io.terminus.dalaran.model.flow;

import io.terminus.dalaran.DalaranConstants;
import io.terminus.dalaran.model.MessageModel;
import io.terminus.dalaran.model.component.ProcessorModel;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.terminus.dalaran.DalaranConstants.DIRECT_PREFIX;

@Data
public class BasicFlow {

    @NotNull
    private Long id;

    private String name;

    private String description;

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
