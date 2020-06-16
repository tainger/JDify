package io.terminus.dalaran.mapper.model;

import io.terminus.dalaran.model.MessageModel;
import lombok.Data;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jingdi on 2019/3/18
 */
@Data
public class DalaranMapperConfig {

    private List<SimpleMapping> noDestinationMappings;

    private HashMap<String, SimpleMapping> messageMapping;

    private transient MessageModel inModel;

    private transient MessageModel outModel;
}
