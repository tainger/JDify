package io.terminus.dalaran.core.resource.entity.common;

import io.terminus.dalaran.core.resource.converter.ListToJsonConverter;
import io.terminus.dalaran.core.resource.entity.basic.BasicEntity;
import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;

@Data
@Entity
@Table(name = "dalaran_private_repository")
public class PrivateRepositoryEntity extends BasicEntity {

    @Column(nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 64)
    private String version;

    @Column(nullable = false, length = 64)
    private String type;

    @Column(length = 64)
    private String resourceGroup;

    @Column(nullable = false, length = 64)
    private String tenantCode;

    @Column(length = 64)
    private String origin;

    @Column
    private String logoUri;

    @Convert(converter = ListToJsonConverter.class)
    @Column(length = 256)
    private List<String> label;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String data;

    @Column(columnDefinition = "TEXT")
    private String description;
}
