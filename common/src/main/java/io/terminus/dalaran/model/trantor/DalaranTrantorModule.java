package io.terminus.dalaran.model.trantor;

import lombok.Data;

import java.util.List;

@Data
public class DalaranTrantorModule {

    private String key;

    private String name;

    private List<DalaranIntegrationInfo> integrations;

}
