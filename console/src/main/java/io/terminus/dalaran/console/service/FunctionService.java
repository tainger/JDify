package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.dto.FunctionDTO;
import io.terminus.dalaran.console.model.dto.basic.BasicFunctionInfo;

import java.util.List;

public interface FunctionService {

    Long create(FunctionDTO functionDTO);

    FunctionDTO update(FunctionDTO functionDTO);

    void delete(Long functionId);

    FunctionDTO detail(Long functionId);

    List<BasicFunctionInfo> listBasicInfoByModuleId(Long moduleId);

}
