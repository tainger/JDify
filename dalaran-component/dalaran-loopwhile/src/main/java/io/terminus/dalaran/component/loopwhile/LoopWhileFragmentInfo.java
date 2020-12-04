package io.terminus.dalaran.component.loopwhile;

import lombok.Data;

@Data
public class LoopWhileFragmentInfo {

    private String expression;
    private String routeId;

    public LoopWhileFragmentInfo(String expression, String routeId) {
        this.expression = expression;
        this.routeId = routeId;
    }
}
