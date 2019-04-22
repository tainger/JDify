package io.terminus.dalaran.console.model;

import io.terminus.dalaran.BodyModelType;
import lombok.Data;

@Data
public class TestResult {

    private BodyModelType bodyType;

    private String body;

    private boolean successful;

    private Long logId;

}
