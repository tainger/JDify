package io.terminus.dalaran.rest.read;

import io.swagger.annotations.ApiOperation;
import io.terminus.dalaran.market.model.MarketResourceVersionDTO;
import io.terminus.dalaran.model.dto.PrivateRepositoryDTO;
import io.terminus.dalaran.model.dto.ResourceGroupDTO;
import io.terminus.dalaran.model.query.PrivateRepositoryQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collection;
import java.util.List;

@RequestMapping(value = "/api/repository/private", produces = {"application/json; charset=UTF-8"})
public interface PrivateRepositoryReadAPI {

    @ApiOperation(value = "根据查询条件获取当前环境私仓组件资源列表")
    @GetMapping(value = "/resource")
    Collection<MarketResourceVersionDTO> listPrivateResource(PrivateRepositoryQuery query);

    @ApiOperation(value = "根据资源ID获取资源详情")
    @GetMapping(value = "/resource/{id}/{version}")
    PrivateRepositoryDTO getResourceDetail(@PathVariable String id, @PathVariable String version);

    @ApiOperation(value = "根据资源分组列表")
    @GetMapping(value = "/resource/group")
    List<ResourceGroupDTO> listResourceGroup();
}
