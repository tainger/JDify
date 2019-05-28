package io.terminus.dalaran.model;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class ConnectorModel<T> {

    @Nullable
    private T config;
}
