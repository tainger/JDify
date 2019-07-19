package io.terminus.dalaran.component.processor.mapper.model;

import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/7/17
 */
@Data
public class SourcePath {

    private String path;

    private List<PathDetail> details;

    public SourcePath(String path, List<PathDetail> details) {
        this.path = path;
        this.details = details;
    }
}
