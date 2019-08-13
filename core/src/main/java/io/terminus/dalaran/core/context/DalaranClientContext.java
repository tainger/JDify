package io.terminus.dalaran.core.context;

import java.util.Map;

public interface DalaranClientContext {

    Map<String, String> getAllClient();

    String getSecret(String appKey);

    void addClient(String appKey, String secret);
}
