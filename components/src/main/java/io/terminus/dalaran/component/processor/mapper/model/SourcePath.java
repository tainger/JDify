package io.terminus.dalaran.component.processor.mapper.model;

import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/7/17
 */
@Data
public class SourcePath {

    private String path;

    private List<String> details;

    public SourcePath(String path, List<String> details) {
        this.path = path;
        this.details = details;
    }
}
