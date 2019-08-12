package io.terminus.dalaran.core.context.support;

import io.terminus.dalaran.core.context.DalaranClientContext;

import java.util.HashMap;
import java.util.Map;

public class DefaultDalaranClientContext implements DalaranClientContext {

    private final Map<String, String> clientMapper = new HashMap<>();

    @Override
    public Map<String, String> getAllClient() {
        return new HashMap<>(clientMapper);
    }

    @Override
    public String getSecret(String clientId) {
        return clientMapper.get(clientId);
    }

    @Override
    public void addClient(String clientId, String secret) {
        clientMapper.put(clientId, secret);
    }
}
