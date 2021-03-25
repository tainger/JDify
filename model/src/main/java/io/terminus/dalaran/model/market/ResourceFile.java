package io.terminus.dalaran.model.market;

import lombok.Data;

@Data
public class ResourceFile {

    private String filePath;

    public ResourceFile() {
    }

    public ResourceFile(String filePath) {
        this.filePath = filePath;
    }
}
