package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;
import io.terminus.dalaran.model.dto.flow.BasicFlowDTO;

import java.util.List;

public class ModuleFlowDTO {

    private String ModuleName;

    private String id;

    private List<BasicFlowInfo> basicFlowInfos;


    public String getModuleName() {
        return ModuleName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<BasicFlowInfo> getBasicFlowInfos() {
        return basicFlowInfos;
    }

    public void setBasicFlowInfos(List<BasicFlowInfo> basicFlowInfos) {
        this.basicFlowInfos = basicFlowInfos;
    }


    public void setModuleName(String moduleName) {
        ModuleName = moduleName;
    }

    @Override
    public String toString() {
        return "ModuleFlowDTO{" +
                "ModuleName='" + ModuleName + '\'' +
                ", basicFlowInfos=" + basicFlowInfos +
                '}';
    }
}


