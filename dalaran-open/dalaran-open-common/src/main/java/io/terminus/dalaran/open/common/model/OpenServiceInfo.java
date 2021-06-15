package io.terminus.dalaran.open.common.model;

import io.terminus.dalaran.open.common.service.DalaranOpenService;
import lombok.Data;

@Data
public class OpenServiceInfo {

    private DalaranOpenService bean;

    private Class inModel;

    private Class outModel;

    public OpenServiceInfo(DalaranOpenService bean, Class inModel, Class outModel) {
        this.bean = bean;
        this.inModel = inModel;
        this.outModel = outModel;
    }
}
