package io.terminus.dalaran;

import java.util.Map;

public interface DalaranTrigger<T> {
    String getUri(Map<String, String> properties, T config);
}
