package io.terminus.dalaran.entity.manage;

import io.terminus.dalaran.BodyType;
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

    @Column(nullable = false)
    private Long flowId;

    @Column(length = 64)
    private String processorId;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private Long elapsed;

    @Column(nullable = false, length = 64)
    private String recordId;

    @Column(nullable = false)
    private boolean successful;

    @Column(nullable = false)
    private boolean main;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TracingType tracingType;

    @Column(columnDefinition = "LONGTEXT")
    private String inputBody;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BodyType inputBodyType;

    @Column(columnDefinition = "LONGTEXT")
    private String outputBody;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BodyType outputBodyType;
}
