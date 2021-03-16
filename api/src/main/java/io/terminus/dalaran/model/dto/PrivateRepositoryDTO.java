package io.terminus.dalaran.model.dto;

import io.terminus.dalaran.market.model.BasicResourceDTO;
import lombok.Data;

import java.util.Map;

@Data
public class PrivateRepositoryDTO extends BasicResourceDTO {

    private Map<String, Object> data;
}
