package io.terminus.dalaran.model.component;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class ServiceModel<T> {

    @Nullable
    private T serviceConfig;
}
