package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.service.PrivateRepositoryService;
import io.terminus.dalaran.core.resource.repository.PrivateRepositoryRepository;
import io.terminus.dalaran.market.model.BasicResourceDTO;
import io.terminus.dalaran.market.model.MarketResourceVersionDTO;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.dto.PrivateRepositoryDTO;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collection;

@Service
public class PrivateRepositoryServiceImpl implements PrivateRepositoryService {

    @Autowired
    private PrivateRepositoryRepository privateRepository;

    @Override
    public Collection<MarketResourceVersionDTO> listPrivateResource(PrivateRepositoryQuery query) {
        return null;
    }

    @Override
    public PrivateRepositoryDTO getResourceDetail(String id, String version) {
        return null;
    }

    @Override
    public BasicResponse publish(BasicResourceDTO basicResource) {
        PrivateRepositoryDTO privateResource = getResourceDetail(basicResource.getId(), basicResource.getVersion());

        return null;
    }

    @Override
    public BasicResponse install(PrivateRepositoryDTO privateRepository) {
        return null;
    }
}
