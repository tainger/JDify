package io.terminus.dalaran.model;

import lombok.Getter;

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
}
