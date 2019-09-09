package io.terminus.dalaran.console.model.dto;

import io.terminus.dalaran.console.model.ModelImportMode;
import lombok.Data;

@Data
public class ImportInfo {
    private Long moduleId;
    private ModelImportMode importMode;
    private ModelDTO inModel;
    private ModelDTO outModel;
}
