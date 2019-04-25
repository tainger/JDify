package io.terminus.dalaran.publish.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
//@Entity
//@Table(name = "dalaran_publish_record")
public class PublishRecordEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String version;

    private boolean successful;

    private String publishLog;

    private Long operator;

    private Date createdAt;

}
