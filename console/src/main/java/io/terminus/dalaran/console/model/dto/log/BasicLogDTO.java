package io.terminus.dalaran.console.model.dto.log;

import io.terminus.dalaran.core.model.BodyType;
import lombok.Data;

import java.util.Date;

@Data
public class BasicLogDTO {
    private Long id;

    private String recordId;

    private boolean successful;

    private Long flowId;

    private String flowName;

    private Date timestamp;

    private Long elapsed;

    private BodyType inputBodyType;

    private BodyType outputBodyType;

    private String inputBody;

    private String outputBody;
}
