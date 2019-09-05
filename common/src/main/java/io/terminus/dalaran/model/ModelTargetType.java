package io.terminus.dalaran.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ModelTargetType {

    Normal, Service(false), Trantor(false);

    @Getter
    private boolean editable;

    ModelTargetType(boolean editable) {
        this.editable = editable;
    }

    ModelTargetType() {
        this.editable = true;
    }

    public static List<ModelTargetType> editableTypes() {
        return Arrays.stream(ModelTargetType.values()).filter(ModelTargetType::isEditable).collect(Collectors.toList());
    }
}
