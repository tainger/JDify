package io.terminus.dalaran.core.resource.entity.common;

import io.terminus.dalaran.TracingType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "dalaran_tracing_log", indexes={@Index(name = "recordId", columnList = "recordId")})
public class TracingLogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long moduleId;

    @Column(nullable = false)
    private Long flowId;

    @Column(length = 64)
    private String processorId;

    @Column(nullable = false)
    private Long timestamp;

    @Column(nullable = false)
    private Long elapsed;

    @Column(nullable = false, length = 128)
    private String recordId;

    @Column(nullable = false)
    private boolean successful;

    @Column(nullable = false)
    private boolean main;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TracingType tracingType;

    @Column(columnDefinition = "LONGTEXT")
    private String inputBody;

    @Column(nullable = false, length = 32)
    private String inputBodyType;

    @Column(columnDefinition = "LONGTEXT")
    private String outputBody;

    @Column(nullable = false, length = 32)
    private String outputBodyType;

    @CreatedDate
    @Column(columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt = new Date();

    private String version;
}
