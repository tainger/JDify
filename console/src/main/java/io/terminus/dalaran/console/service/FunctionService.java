package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.FunctionDTO;
import io.terminus.dalaran.model.dto.basic.BasicFunctionInfo;

import java.util.List;

public interface FunctionService {

    String create(FunctionDTO functionDTO);

    FunctionDTO update(FunctionDTO functionDTO);

    void delete(String functionId);

    FunctionDTO detail(String functionId);

    List<BasicFunctionInfo> listBasicInfoByModuleId(String moduleId);

}
