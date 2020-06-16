package io.terminus.dalaran.mapper.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingdi on 2019/7/18
 */
@Data
public class DalaranMappingConfig {

    private SimpleMappingField sourceRoot;

    private SimpleMappingField destinationRoot;

    private List<MessageMapping> messageMappings = new ArrayList<>();
}
