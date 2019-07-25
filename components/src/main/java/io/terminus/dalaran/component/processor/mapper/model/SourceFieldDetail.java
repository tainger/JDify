package io.terminus.dalaran.component.processor.mapper.model;

import lombok.Data;

import java.util.List;

/**
 * Created by jingdi on 2019/7/23
 */
@Data
public class SourceFieldDetail {

    private List<Integer> arrayFieldSize;

    private List<List<SourcePath>> sourcePaths;

    public SourceFieldDetail(List<Integer> arrayFieldSize, List<List<SourcePath>> sourcePaths) {
        this.arrayFieldSize = arrayFieldSize;
        this.sourcePaths = sourcePaths;
    }
}
