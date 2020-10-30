package io.terminus.dalaran.model.dto.log;


import lombok.Data;

import java.util.Date;

@Data
public class BasicLogDTO {
    private Long id;

    private String recordId;

    private boolean successful;

    private Long flowId;

    private String flowName;

    private Date createdAt;

    private Date timestamp;

    private Long elapsed;

    private String inputBodyType;

    private String outputBodyType;

    private String inputBody;

    private String outputBody;
}
