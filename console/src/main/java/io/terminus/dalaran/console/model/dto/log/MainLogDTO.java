package io.terminus.dalaran.console.model.dto.log;

import lombok.Data;

import java.util.List;

@Data
public class MainLogDTO extends BasicLogDTO {

    private Long moduleId;

    private String moduleName;

    private List<TracingLogDTO> tracingLogList;
}
