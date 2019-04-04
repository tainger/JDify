package io.terminus.dalaran.console.model.query;

import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/3/28
 */
@Data
public class StructureQuery {

    private Long moduleId;

    private List<Long> structureIds;

    private String name;
}
