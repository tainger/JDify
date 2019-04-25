package io.terminus.dalaran.entity;

import io.terminus.dalaran.BodyModelType;
import io.terminus.dalaran.TracingType;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "dalaran_tracing_log")
public class TracingLogEntity {
    @Id
    @GeneratedValue
    private Long id;

    private Long flowId;

    private Long processorId;

    private Long timestamp;

    private Long elapsed;

    private String recordId;

    @Column(nullable = false)
    private Boolean successful;

    @Column(nullable = false)
    private boolean main;

    @Enumerated(EnumType.STRING)
    private TracingType tracingType;

    private String inputBody;

    @Enumerated(EnumType.STRING)
    private BodyModelType inputBodyType;

    private String outputBody;

    @Enumerated(EnumType.STRING)
    private BodyModelType outputBodyType;
}
