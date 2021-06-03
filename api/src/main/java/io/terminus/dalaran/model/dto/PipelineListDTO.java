package io.terminus.dalaran.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PipelineListDTO {

    List<ProcessorDTO> pipeline;
}
