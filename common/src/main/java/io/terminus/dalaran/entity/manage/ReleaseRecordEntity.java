package io.terminus.dalaran.entity.manage;

import io.terminus.dalaran.entity.BasicEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "dalaran_release_record")
public class ReleaseRecordEntity extends BasicEntity {

    @Column(unique = true, nullable = false, length = 64)
    private String version;

    @Column(nullable = false)
    private boolean successful;

    @Column(nullable = false)
    private boolean enabled;

    @Column(columnDefinition = "TEXT")
    private String releaseLog;

    private Long operator;

    private Date releaseTime;

}
