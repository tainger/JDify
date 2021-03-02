package io.terminus.dalaran.console.entity;

import io.terminus.dalaran.core.resource.entity.AlarmRuleAbstractEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_alarm_rule")
public class AlarmRuleEntity extends AlarmRuleAbstractEntity {
}
