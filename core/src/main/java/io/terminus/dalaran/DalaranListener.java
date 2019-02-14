package io.terminus.dalaran;

import java.util.Map;

public interface DalaranListener<T> {
    String getUri(Map<String, String> properties, T config);
}
