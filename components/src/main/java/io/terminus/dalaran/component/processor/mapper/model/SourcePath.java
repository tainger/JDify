package io.terminus.dalaran.component.processor.mapper.model;

import lombok.Data;

/**
 * Created by jingdi on 2019/7/17
 */
@Data
public class SourcePath {

    private String path;

    private String originPath;

    private PathDetail detail;

    public SourcePath(String path, PathDetail detail) {
        this.path = path;
        this.detail = detail;
    }
}
