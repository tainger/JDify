package io.terminus.dalaran;

public interface DalaranComponentConfigImporter<Config, Import> {

    Config importConfig(Import importConfig);
}
