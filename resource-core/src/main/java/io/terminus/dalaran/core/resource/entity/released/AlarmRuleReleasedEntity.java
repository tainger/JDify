package io.terminus.dalaran.core.resource.entity.released;

import io.terminus.dalaran.core.resource.entity.AlarmRuleAbstractEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_released_alarm_rule")
public class AlarmRuleReleasedEntity extends AlarmRuleAbstractEntity implements ReleasedEntity{
    @Column(nullable = false)
    private String originId;

    @Column(nullable = false, length = 64)
    private String version;

}
