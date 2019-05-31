package io.terminus.dalaran.core.flow;

import io.terminus.dalaran.core.model.MessageModel;
import lombok.Data;
import org.apache.camel.model.RouteDefinition;

@Data
public class DalaranRoute extends RouteDefinition {

    private boolean serializedBody;

    private MessageModel lastOutModel;
}
