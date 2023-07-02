package io.terminus.dalaran.console.service;

import io.terminus.dalaran.console.model.FlowTemplate;
import io.terminus.dalaran.model.BasicResponse;
import io.terminus.dalaran.model.ResourceUploadRequest;
import io.terminus.dalaran.model.dto.BasicResourceRequest;
import io.terminus.dalaran.model.dto.PrivateRelationResource;
import io.terminus.dalaran.model.dto.ResourceGroupDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PrivateRepositoryService {

//    Collection<MarketResourceVersionDTO> listPrivateResource(PrivateRepositoryQuery query);
//
//    Page<MarketResourceVersionDTO> pagingPrivateResource(PrivateRepositoryQuery query, Integer pageNumber, Integer pageSize);
//
//    PrivateRepositoryDTO getResourceDetail(String id, String version);
//
//    BasicResponse publish(BasicResourceDTO basicResource);
//
//    BasicResponse install(PrivateRepositoryDTO privateRepository);

    BasicResponse saveTemplate(FlowTemplate flowTemplate);

    BasicResponse localResourceUpload(MultipartFile file, String name, String version, String resourceGroup);

    BasicResponse localResourceUpload(ResourceUploadRequest resourceUploadRequest);

    List<ResourceGroupDTO> listResourceGroup();

    BasicResponse delete(BasicResourceRequest request);

    PrivateRelationResource listPrivateRelationResource() throws Exception;
}
