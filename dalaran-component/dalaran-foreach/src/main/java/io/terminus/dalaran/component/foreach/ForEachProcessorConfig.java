package io.terminus.dalaran.component.foreach;

import io.terminus.dalaran.core.flow.DalaranRoute;
import lombok.Data;

@Data
public class ForEachProcessorConfig {

    public ForEachProcessorConfig(String fragmentUri, DalaranRoute route) {
        this.fragmentUri = fragmentUri;
        this.route = route;
    }

    private String fragmentUri;

    private DalaranRoute route;
}
