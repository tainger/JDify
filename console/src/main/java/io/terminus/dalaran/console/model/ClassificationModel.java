package io.terminus.dalaran.console.model;

import io.terminus.dalaran.console.model.dto.ModelDTO;
import lombok.Data;

import java.util.List;

@Data
public class ClassificationModel {

    private String name;

    private ServiceType serviceType;

    private List<ModelDTO> models;
}
