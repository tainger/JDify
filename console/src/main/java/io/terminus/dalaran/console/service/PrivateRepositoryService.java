package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.FlowTemplate;
import io.terminus.dalaran.market.model.BasicResourceDTO;
import io.terminus.dalaran.market.model.MarketResourceVersionDTO;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.dto.PrivateRepositoryDTO;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;

import java.util.Collection;

public interface PrivateRepositoryService {

    Collection<MarketResourceVersionDTO> listPrivateResource(PrivateRepositoryQuery query);

    PrivateRepositoryDTO getResourceDetail(String id, String version);

    BasicResponse publish(BasicResourceDTO basicResource);

    BasicResponse install(PrivateRepositoryDTO privateRepository);

    BasicResponse saveTemplate(FlowTemplate flowTemplate);

}
