package io.terminus.dalaran.console.model.dto.flow;

import io.terminus.dalaran.console.model.dto.ProcessorDTO;
import io.terminus.dalaran.model.ProcessorModel;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
public class BasicFlowDTO {

    @Nullable
    private Long id;

    private Long moduleId;

    private String name;

    private String description;

    private Long inModelId;

    private Long outModelId;

    // TODO processor 有一个流内的唯一 ID, pipeline 就是由这个 ID 编排的, 该 ID 可以由前端生成, 否则追踪日志没有标识
    private List<ProcessorDTO> pipeline;

}
