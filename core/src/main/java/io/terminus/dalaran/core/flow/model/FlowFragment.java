package io.terminus.dalaran.core.flow.model;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

import static io.terminus.dalaran.core.DalaranConstants.DELIMITER;
import static io.terminus.dalaran.core.DalaranConstants.FLOW_FRAGMENT_PREFIX;

@Data
public class FlowFragment extends BasicFlow {
    private static final AtomicInteger fragmentMiddleIdGenerator = new AtomicInteger();

    private final Integer fragmentId = fragmentMiddleIdGenerator.incrementAndGet();

    @Override
    public String getRouteId() {
        return FLOW_FRAGMENT_PREFIX + this.getId() + DELIMITER + fragmentId;
    }
}
