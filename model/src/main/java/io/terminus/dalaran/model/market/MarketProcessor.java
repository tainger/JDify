package io.terminus.dalaran.model.market;

import io.terminus.dalaran.market.model.BasicResourceDTO;
import lombok.Data;

@Data
public class MarketProcessor extends BasicResourceDTO {

    private ResourceFile data;
}
