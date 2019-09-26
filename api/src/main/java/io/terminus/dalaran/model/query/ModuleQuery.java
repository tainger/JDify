package io.terminus.dalaran.model.query;

import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/4/1
 */
@Data
public class ModuleQuery {

    private List<Long> moduleIds;

    private String name;
}
