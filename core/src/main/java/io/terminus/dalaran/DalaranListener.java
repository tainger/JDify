package io.terminus.dalaran;

import java.util.Map;

public interface DalaranListener {
    String getUri(Map<String, String> properties);
}
