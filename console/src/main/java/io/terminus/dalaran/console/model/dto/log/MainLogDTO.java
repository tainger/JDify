package io.terminus.dalaran.console.model.dto.log;

import lombok.Data;

import java.util.List;

@Data
public class MainLogDTO extends BasicLogDTO {

    private List<TracingLogDTO> tracingLogList;
}
