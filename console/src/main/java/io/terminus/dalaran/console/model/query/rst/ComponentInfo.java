package io.terminus.dalaran.console.model.query.rst;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by jingdi on 2019/4/10
 */
@Data
public class ComponentInfo implements Serializable {

    private String name;

    private String status;

    public ComponentInfo(String name, String status) {
        this.name = name;
        this.status = status;
    }
}
