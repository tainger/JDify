package io.terminus.dalaran.component.message.convert.custom;

import io.terminus.dalaran.message.MessageMapping;
import lombok.Data;

/**
 * Created by jingdi on 2019/3/18
 */
@Data
public class CustomMapperConfig {
    private MessageMapping messageMapping;

    public MessageMapping getMessageMapping() {
        return messageMapping;
    }

    public void setMessageMapping(MessageMapping messageMapping) {
        this.messageMapping = messageMapping;
    }
}
