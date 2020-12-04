package io.terminus.dalaran.component.service;

import io.terminus.dalaran.model.flow.FlowValidateMessage;
import io.terminus.dalaran.model.flow.ValidateMessageLevel;

public class ServiceValidationMessages {

    protected static final FlowValidateMessage SERVICE_NOT_EXIST =
            new FlowValidateMessage(ValidateMessageLevel.Error, "Service.Not.Exist", "服务不存在, 请重新其他服务");

    protected static final FlowValidateMessage OPERATION_NOT_EXIST =
            new FlowValidateMessage(ValidateMessageLevel.Error, "Operation.Not.Exist", "服务操作不存在, 请重新其他操作");

}
