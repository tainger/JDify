package io.terminus.dalaran.impl;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public class DalaranComponentContainer<T> {

    @NotNull
    private String type;
    @NotNull
    private Class componentClass;
    @NotNull
    private T component;
    @Nullable
    private Class configClass;


    public DalaranComponentContainer(@NotNull String type,  @Nullable Class configClass, @NotNull T component) {
        this.type = type;
        this.componentClass = component.getClass();
        this.configClass = configClass;
        this.component = component;
    }
}
