package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.dto.BasicFunctionInfo;
import io.terminus.dalaran.console.model.dto.FunctionDTO;

import java.util.List;

public interface FunctionService {

    Long create(FunctionDTO functionDTO);

    FunctionDTO update(FunctionDTO functionDTO);

    void delete(Long functionId);

    FunctionDTO detail(Long functionId);

    List<BasicFunctionInfo> listBasicInfoByModuleId(Long moduleId);

}
