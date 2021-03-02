package io.terminus.dalaran.model.dto.basic;

import lombok.Data;

import java.util.Date;

@Data
public class BasicAlarmInfo {

    private Long id;

    private Long moduleId;

    private String moduleName;

    private String name;

    private Date createTime;

    private Date modifyTime;

}
