package io.terminus.dalaran.model.dto.log;

import lombok.Data;

import java.util.List;

@Data
public class MainLogDTO extends BasicLogDTO {

    private String moduleId;

    private String moduleName;

    private List<TracingLogDTO> tracingLogList;
}
