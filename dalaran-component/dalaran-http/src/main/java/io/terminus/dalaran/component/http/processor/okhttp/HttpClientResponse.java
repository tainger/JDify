package io.terminus.dalaran.component.http.processor.okhttp;


import lombok.Data;

@Data
public class HttpClientResponse {

    private Integer responseCode;

    private String responseBody;


    public HttpClientResponse(Integer responseCode, String responseBody) {
        this.responseCode = responseCode;
        this.responseBody = responseBody;
    }
}
