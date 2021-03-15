package io.terminus.dalaran.response;

import io.terminus.dalaran.model.dto.flow.TriggerFlowDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ResponseResult <T>{

    private boolean success = true;

    private boolean isDelete = true;

    private String errorMsg;

    private List<T> data;
}
