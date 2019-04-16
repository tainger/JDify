package io.terminus.dalaran.console.model.query.rst;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by jingdi on 2019/4/10
 */
@Data
public class ComponentType implements Serializable {

    private String type;

    public ComponentType(String type) {
        this.type = type;
    }
}
