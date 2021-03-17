package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.market.model.BasicResourceDTO;
import io.terminus.dalaran.market.model.MarketResourceVersionDTO;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.dto.PrivateRepositoryDTO;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import io.terminus.dalaran.rest.read.PrivateRepositoryReadAPI;
import io.terminus.dalaran.rest.write.PrivateRepositoryWriteAPI;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collection;

@RestController
public class PrivateRepositoryRest implements PrivateRepositoryReadAPI, PrivateRepositoryWriteAPI {

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
        return null;
    }
}
