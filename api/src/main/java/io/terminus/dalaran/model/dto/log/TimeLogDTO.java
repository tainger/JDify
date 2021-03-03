package io.terminus.dalaran.model.dto.log;


import lombok.Data;

@Data
public class TimeLogDTO {

    private Long maxTime;

    private Double avgTime;

    public TimeLogDTO(){

    }

    public TimeLogDTO(Long maxTime, Double avgTime){
        this.maxTime = maxTime;
        this.avgTime = avgTime;
    }

}
