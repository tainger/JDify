package io.terminus.dalaran.core.component.model;

import io.terminus.dalaran.core.flow.model.BasicFlow;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class TriggerModel<T> extends ComponentModel<T> {
    @NotNull
    private BasicFlow flow;
}