package io.terminus.dalaran.component.ftp.processor;

import lombok.Getter;

public enum FileNameConnector {
    UNDER_LINE("_"), DOT("."), MIDDLE_LINE("-");

    @Getter
    private String value;

    FileNameConnector(String value) {
        this.value = value;
    }
}
