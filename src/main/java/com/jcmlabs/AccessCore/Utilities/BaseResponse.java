package com.jcmlabs.AccessCore.Utilities;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {

    private Response response;
    private T data;
    private List<T> dataList;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        private Boolean success;
        private Integer code;
        private String message;
    }

    public BaseResponse(Boolean success, Integer code, String message){
        this.response = new Response(success,code,message);
    }

    public BaseResponse(Boolean success, Integer code, String message, T data){
        this.response = new Response(success,code,message);
        this.data = data;
    }

    public BaseResponse(Boolean success, Integer code, String message, List<T> data){
        this.response = new Response(success,code,message);
        this.dataList = data;
    }
    
}
