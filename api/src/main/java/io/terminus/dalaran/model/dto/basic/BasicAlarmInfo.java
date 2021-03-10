package io.terminus.dalaran.model.dto.basic;

import lombok.Data;

import java.util.Date;

@Data
public class BasicAlarmInfo {

    private String id;

    private String name;

    private Date createTime;

    private Date modifyTime;

}
