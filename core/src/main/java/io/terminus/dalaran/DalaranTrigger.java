package io.terminus.dalaran;

public interface DalaranTrigger<T> {
    String buildRouterUri(T config);
}
