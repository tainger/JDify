package io.terminus.dalaran.component.ftp.processor.list;

import lombok.Data;

@Data
public class RemoteFileInfo {

    private String name;

    private String path;

    public RemoteFileInfo() {
    }

    public RemoteFileInfo(String name, String path) {
        this.name = name;
        this.path = path;
    }
}
