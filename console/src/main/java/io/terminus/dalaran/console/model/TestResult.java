package io.terminus.dalaran.console.model;

import io.terminus.dalaran.BodyType;
import lombok.Data;

@Data
public class TestResult {

    private BodyType bodyType;

    private String body;

    private boolean successful;

    private String logRecordId;

}
