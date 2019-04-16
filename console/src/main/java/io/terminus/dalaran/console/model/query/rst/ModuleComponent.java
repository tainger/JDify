package io.terminus.dalaran.console.model.query.rst;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jingdi on 2019/4/10
 */
@Data
public class ModuleComponent implements Serializable {

    private String type;

    private String status;

    private List<ComponentInfo> components;

    public ModuleComponent() {
    }
}
