package io.terminus.dalaran.console.service.jpa;

import io.terminus.dalaran.console.model.dto.flow.BasicFlowInfo;

import java.util.List;

public interface SubFlowQueryService {

    List<BasicFlowInfo> listBasicInfoByModuleId(Long moduleId);
}
