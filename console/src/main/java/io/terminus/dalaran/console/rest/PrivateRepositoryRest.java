package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.service.PrivateRepositoryService;
import io.terminus.dalaran.market.model.BasicResourceDTO;
import io.terminus.dalaran.market.model.MarketResourceVersionDTO;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.dto.PrivateRepositoryDTO;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import io.terminus.dalaran.rest.read.PrivateRepositoryReadAPI;
import io.terminus.dalaran.rest.write.PrivateRepositoryWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;

@RestController
public class PrivateRepositoryRest implements PrivateRepositoryReadAPI, PrivateRepositoryWriteAPI {

    @Autowired
    private PrivateRepositoryService privateRepositoryService;

    @Override
    public Collection<MarketResourceVersionDTO> listPrivateResource(PrivateRepositoryQuery query) {
        return privateRepositoryService.listPrivateResource(query);
    }

    @Override
    public PrivateRepositoryDTO getResourceDetail(String id, String version) {
        return privateRepositoryService.getResourceDetail(id, version);
    }

    @Override
    public BasicResponse publish(BasicResourceDTO basicResource) {
        return privateRepositoryService.publish(basicResource);
    }

    @Override
    public BasicResponse install(PrivateRepositoryDTO privateRepository) {
        return privateRepositoryService.install(privateRepository);
    }

    @Override
    public BasicResponse localUpload(MultipartFile file, BasicResourceDTO basicResource) {
        return privateRepositoryService.localResourceUpload(file, basicResource);
    }
}
