package io.terminus.dalaran.console.model;

import java.util.*;
public class FlowResourceCollector {


    private Map<String, Set<String>> resourceKeyCollector;

    public FlowResourceCollector() {
        resourceKeyCollector = new HashMap<>(16);
    }

    public void collect(String resourceType, String resourceId) {
        Set<String> resourceKeys = resourceKeyCollector.computeIfAbsent(resourceType, k -> new HashSet<>());
        resourceKeys.add(resourceId);
    }

    public Map<String, Set<String>> getResourceKeyCollector() {
        return resourceKeyCollector;
    }
}
