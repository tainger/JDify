package io.terminus.dalaran.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DalaranFlow {
    private DalaranComponentInstance trigger;
    private List<DalaranComponentInstance> processors;
    private Map<String, String> properties;
}
