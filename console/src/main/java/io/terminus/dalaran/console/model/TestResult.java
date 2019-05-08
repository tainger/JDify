package io.terminus.dalaran.console.model;

import io.terminus.dalaran.console.model.dto.log.MainLogDTO;
import lombok.Data;

@Data
public class TestResult {

    private boolean successful;

    private MainLogDTO logDetail;
}
