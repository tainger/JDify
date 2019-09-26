package io.terminus.dalaran.model.query;

import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/4/16
 */
@Data
public class PropertyQuery {

    private List<Long> propertyIds;

    private String name;
}
