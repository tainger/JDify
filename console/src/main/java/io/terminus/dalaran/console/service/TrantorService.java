package io.terminus.dalaran.console.service;

import io.terminus.dalaran.model.dto.trantor.TrantorModuleDTO;
import io.terminus.dalaran.model.trantor.DalaranTrantorModule;

import java.util.List;

public interface TrantorService {
    void saveTrantorIntegrationInfo(DalaranTrantorModule trantorModule);

    List<TrantorModuleDTO> getAllModule();
}
