package io.terminus.dalaran.model.flow;

import lombok.Data;

@Data
public class FlowValidation {

    private String targetId;

    private ValidateMessageTarget targetType;

    private ValidateMessageType type;

    // TODO 嵌套场景可能就比较尴尬了, 要么就是前端保证 processor 不糊重复 id, 要么这个 field 存个 path
    private String field;

    private String message;

    // TODO 该操作的建议, 但是可能拆出去会更好一点
    private String suggest;

}
