package io.terminus.dalaran.model.component;

import lombok.Data;

@Data
public class ProcessorModel<T> extends ComponentModel<T> {

    private String group;

    private String version;
}
