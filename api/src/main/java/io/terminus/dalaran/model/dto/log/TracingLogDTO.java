package io.terminus.dalaran.model.dto.log;

import lombok.Data;

@Data
public class TracingLogDTO extends BasicLogDTO {

    private String processorId;
}
