package io.terminus.dalaran.component.convert;

import io.terminus.dalaran.core.component.DalaranProcessor;
import lombok.Data;

@Data
public class TestProcessor {

    private DalaranProcessor processor;

    private Object config;
}
