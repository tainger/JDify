package io.terminus.dalaran.model.dto.flow;

import io.terminus.dalaran.model.dto.NodeDTO;
import io.terminus.dalaran.model.dto.ProcessorDTO;
import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BasicFlowDTO extends BasicFlowInfo {

    private String description;

    private String inModelId;

    private String outModelId;

    // TODO processor 有一个流内的唯一 ID, pipeline 就是由这个 ID 编排的, 该 ID 可以由前端生成, 否则追踪日志没有标识
    private List<ProcessorDTO> pipeline = new ArrayList<>();

    private List<NodeDTO> node = new ArrayList();

}
