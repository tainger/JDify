package io.terminus.dalaran.console.service.impl;

import io.terminus.dalaran.console.service.MarketManagementService;
import io.terminus.dalaran.core.market.MarketResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class MarketManagementServiceImpl implements MarketManagementService {

    @Autowired
    private MarketResourceLoader marketResourceLoader;

    @Override
    public void upload(String url) {
        File localFile = new File("/Users/jingdi/work/terminus-work/Trantor/dice/current/dalaran-starter/custom-components/target/custom-components-2.4.8-SNAPSHOT.jar");
        marketResourceLoader.loadProcessor(localFile);
    }
}
