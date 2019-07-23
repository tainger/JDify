package io.terminus.dalaran.model.component;

import io.terminus.dalaran.model.flow.BasicFlow;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class TriggerModel<T> extends ComponentModel<T> {
    @NotNull
    private BasicFlow flow;
}