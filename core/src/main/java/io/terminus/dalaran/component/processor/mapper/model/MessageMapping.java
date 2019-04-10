package io.terminus.dalaran.processor.mapper.model;

import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/3/13
 */
@Data
public class MessageMapping {

    private DalaranMessage target;

    private DalaranMessage destination;

    private List<FieldMapping> mappings;

    private List<SingleFieldMapping> singleFieldMappings;
}
