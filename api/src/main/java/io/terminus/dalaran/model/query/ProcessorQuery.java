package io.terminus.dalaran.model.query;

import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/3/28
 */
@Data
public class ProcessorQuery {

    private List<Long> processorIds;

    private String type;

    private Long moduleId;

    private String name;
}
