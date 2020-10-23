package io.terminus.dalaran.model.dto.log;


import lombok.Data;

@Data
public class TimeLogDTO {

    private Long maxTime;

    private Long minTime;

    private Double avgTime;

    private Long count;

    public TimeLogDTO(){

    }

    public TimeLogDTO(Long maxTime,Long minTime,Double avgTime,Long count){
        this.maxTime = maxTime;
        this.minTime = minTime;
        this.avgTime = avgTime;
        this.count = count;
    }

}
