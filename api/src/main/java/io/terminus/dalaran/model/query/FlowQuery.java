package io.terminus.dalaran.model.query;

import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/3/28
 */
@Data
public class FlowQuery {

    private List<Long> flowIds;

    private Long moduleId;

    private String type;

    private String name;
}
