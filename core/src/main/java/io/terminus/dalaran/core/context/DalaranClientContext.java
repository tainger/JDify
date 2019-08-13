package io.terminus.dalaran.core.context;

import java.util.Map;

public interface DalaranClientContext {

    Map<String, String> getAllClient();

    String getSecret(String clientId);

    void addClient(String clientId, String secret);
}
