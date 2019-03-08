package io.terminus.dalaran;

public interface DalaranTrigger<T> {
    String buildFromUri(T config);
}
