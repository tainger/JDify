package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.model.dto.basic.BasicFlowInfo;

import java.util.List;

public interface SubFlowQueryService {

    List<BasicFlowInfo> listBasicInfoByModuleId(Long moduleId);
}
