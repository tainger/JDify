package io.terminus.dalaran.rest.read;

import io.terminus.dalaran.model.dto.NodeDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping(value = "/api/node", produces = {"application/json; charset=UTF-8"})
public interface NodeReadAPI {

    @GetMapping("/pageable")
    Page<NodeDTO> queryPageable(@RequestParam Integer pageNumber, @RequestParam Integer pageSize);
}
