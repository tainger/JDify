package io.terminus.dalaran.component.processor.mapper.model;

import lombok.Data;

/**
 * Created by jingdi on 2019/5/14
 */
@Data
public class Flag {

    private boolean value;

    public Flag(boolean value) {
        this.value = value;
    }
}
