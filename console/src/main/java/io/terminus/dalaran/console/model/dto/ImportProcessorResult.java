package io.terminus.dalaran.console.model.dto;

import lombok.Data;

@Data
public class ImportProcessorResult extends ImportResult {
    private ProcessorDTO processor;
}
