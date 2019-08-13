package io.terminus.dalaran.console.model.dto.basic;

import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
public class BasicServiceInfo {

    @Nullable
    private Long id;

    private Long moduleId;

    private String type;

    private String name;

    public BasicServiceInfo() {
    }

    public BasicServiceInfo(@Nullable Long id, Long moduleId, String type, String name) {
        this.id = id;
        this.moduleId = moduleId;
        this.type = type;
        this.name = name;
    }
}
