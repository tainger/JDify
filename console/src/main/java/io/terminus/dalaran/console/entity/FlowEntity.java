package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.console.JsonConverter;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class FlowEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private TriggerEntity trigger;

    @Convert(converter = JsonConverter.class)
    private List<Long> processors = new ArrayList<>();

    @Convert(converter = JsonConverter.class)
    private List<Long> properties = new ArrayList<>();
}
