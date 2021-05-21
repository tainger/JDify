package io.terminus.dalaran.model.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@Data
@Slf4j
public class NodeDTO{

    private String resourceKey;

    private String name;

    private String company;

    private String application;

    private String system;

    @Nullable
    private Map<String, Object> config;

    private void main(String[] args) {
        log.info("");
    }
}
