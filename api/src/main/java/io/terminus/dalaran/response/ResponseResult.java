package io.terminus.dalaran.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseResult{

    private boolean success = true;

    private String errorMsg;

}
