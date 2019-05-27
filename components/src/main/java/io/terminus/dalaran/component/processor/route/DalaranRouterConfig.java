package io.terminus.dalaran.component.processor.route;

import io.terminus.dalaran.config.OutModelConfig;
import lombok.Data;

import java.util.List;

@Data
public class DalaranRouterConfig extends OutModelConfig {

    private List<DalaranRoute> routes;
}
