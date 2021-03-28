package io.terminus.dalaran.console.rest;

import io.terminus.dalaran.console.service.PrivateRepositoryService;
import io.terminus.dalaran.console.service.jpa.PrivateResourceQueryService;
import io.terminus.dalaran.market.model.BasicResourceDTO;
import io.terminus.dalaran.market.model.MarketResourceVersionDTO;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.dto.BasicResourceRequest;
import io.terminus.dalaran.model.dto.PrivateRepositoryDTO;
import io.terminus.dalaran.model.dto.ResourceGroupDTO;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import io.terminus.dalaran.rest.read.PrivateRepositoryReadAPI;
import io.terminus.dalaran.rest.write.PrivateRepositoryWriteAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

@RestController
public class PrivateRepositoryRest implements PrivateRepositoryReadAPI, PrivateRepositoryWriteAPI {

    @Autowired
    private PrivateRepositoryService privateRepositoryService;

    @Autowired
    private PrivateResourceQueryService privateResourceQueryService;

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
    public BasicResponse localUpload(MultipartFile file, String name,  String version, String resourceGroup) {
        return privateRepositoryService.localResourceUpload(file, name, version, resourceGroup);
    }

    @Override
    public List<ResourceGroupDTO> listResourceGroup() {
        return privateRepositoryService.listResourceGroup();
    }

    @Override
    public List<String> listResourceVersion(String id) {
        return privateResourceQueryService.listResourceVersion(id);
    }

    @Override
    public BasicResponse delete(BasicResourceRequest request) {
        return privateRepositoryService.delete(request);
    }
}
