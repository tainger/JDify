package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.ModelImportMode;
import lombok.Data;

@Data
public class ImportInfo {
    private String moduleId;
    private ModelImportMode importMode;
    private ModelDTO inModel;
    private ModelDTO outModel;
}
