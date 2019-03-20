package io.terminus.dalaran.message;

import lombok.Data;

import java.util.Map;

/**
 * Created by jingdi on 2019/3/12
 */
@Data
public class  DalaranMessage {

    private Map<String, Object> fields;

    private ModelType type;
}
