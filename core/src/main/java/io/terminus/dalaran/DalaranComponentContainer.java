package io.terminus.dalaran;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DalaranComponentContainer<T> {

    @NotNull
    private String type;
    @NotNull
    private Class componentClass;
    @Nullable
    private Class configClass;

    @NotNull
    private T component;

    public DalaranComponentContainer(@NotNull String type, @NotNull Class componentClass, @Nullable Class configClass, @NotNull T component) {
        this.type = type;
        this.componentClass = componentClass;
        this.configClass = configClass;
        this.component = component;
    }

    @NotNull
    public String getType() {
        return type;
    }

    public void setType(@NotNull String type) {
        this.type = type;
    }

    @NotNull
    public Class getComponentClass() {
        return componentClass;
    }

    public void setComponentClass(@NotNull Class componentClass) {
        this.componentClass = componentClass;
    }

    @Nullable
    public Class getConfigClass() {
        return configClass;
    }

    public void setConfigClass(@Nullable Class configClass) {
        this.configClass = configClass;
    }

    @NotNull
    public T getComponent() {
        return component;
    }

    public void setComponent(@NotNull T component) {
        this.component = component;
    }
}
