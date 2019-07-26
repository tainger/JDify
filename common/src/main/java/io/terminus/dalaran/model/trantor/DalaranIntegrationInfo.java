package io.terminus.dalaran.model.trantor;

import lombok.Data;

import java.util.List;

@Data
public class DalaranIntegrationInfo {

    private String key;

    private String name;

    private String description;

    private List<DalaranIntegrationPoint> integrationPoints;
}
