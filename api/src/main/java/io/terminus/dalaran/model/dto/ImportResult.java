package io.terminus.dalaran.model.dto;

import lombok.Data;

import java.util.List;

@Data
public abstract class ImportResult {
    private List<String> existModels;
}
