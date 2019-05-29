package io.terminus.dalaran.core.flow.model;

import io.terminus.dalaran.core.component.model.ProcessorModel;
import io.terminus.dalaran.core.model.MessageModel;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.terminus.dalaran.core.DalaranConstants.DIRECT_PREFIX;

@Data
public abstract class BasicFlow {

    @NotNull
    private Long id;

    private String version;

    @Nullable
    private MessageModel inModel;

    @Nullable
    private MessageModel outModel;

    @NotNull
    private List<ProcessorModel> pipeline;

    public abstract String getRouteId();

    public String getDirectRouteUri() {
        return DIRECT_PREFIX + this.getRouteId();
    }
}
