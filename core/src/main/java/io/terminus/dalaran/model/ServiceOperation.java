package io.terminus.dalaran.model;

import lombok.Data;

@Data
public class ServiceOperation {

    private MessageModel inModel;

    private MessageModel outModel;
}
