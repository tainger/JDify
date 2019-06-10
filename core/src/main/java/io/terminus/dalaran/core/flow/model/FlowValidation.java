package io.terminus.dalaran.core.flow.model;

import io.terminus.dalaran.core.component.ComponentType;
import io.terminus.dalaran.core.flow.ValidateMessageType;
import lombok.Data;

import java.util.List;

@Data
public class FlowValidation {

    private String targetId;

    private ComponentType targetType;

    private ValidateMessageType type;

    // TODO 嵌套场景可能就比较尴尬了, 要么就是前端保证 processor 不糊重复 id, 要么这个 field 存个 path
    private String field;

    private String message;

    // TODO 该操作的建议, 但是可能拆出去会更好一点
    private String suggest;

}
