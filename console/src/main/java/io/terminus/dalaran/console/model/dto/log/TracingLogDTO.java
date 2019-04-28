package io.terminus.dalaran.console.model.dto.log;

import lombok.Data;

@Data
public class TracingLogDTO extends BasicLogDTO {

    private Long processorId;
}
