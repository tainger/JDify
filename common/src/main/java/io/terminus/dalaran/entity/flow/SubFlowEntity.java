package io.terminus.dalaran.entity.flow;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "dalaran_sub_flow")
public class SubFlowEntity extends BasicFlowEntity {
}
