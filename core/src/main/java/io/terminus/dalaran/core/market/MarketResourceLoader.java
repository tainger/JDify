package io.terminus.dalaran.core.market;

import java.io.File;

public interface MarketResourceLoader {

    void install(File file, String group, String version);

    void uninstall(String group, String type, String version);
}
