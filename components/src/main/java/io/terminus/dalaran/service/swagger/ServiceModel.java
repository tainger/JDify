package io.terminus.dalaran.service.swagger;

import io.terminus.dalaran.model.MessageModel;
import lombok.Data;

/**
 * Created by jingdi on 2019/7/24
 */
@Data
public class ServiceModel {

    private String name;

    private MessageModel model;
}
