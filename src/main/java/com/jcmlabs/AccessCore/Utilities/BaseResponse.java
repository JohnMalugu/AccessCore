package com.jcmlabs.AccessCore.Utilities;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {
    private Response response;
    private T data;
    private List<T> dataList;
    private ResponseMetadata metadata;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response{
        private Boolean success;
        private Integer code;
        private String message;
    }
    
    @Data
    @AllArgsConstructor
    public static class ResponseMetadata {
        private Integer number;
        private Boolean hasNextPage;
        private Boolean hasPreviousPage;
        private Integer currentPageNumber;
        private Integer nextPageNumber;
        private Integer previousPageNumber;
        private Integer numberOfPages;
        private Long totalElements;

        public ResponseMetadata(Page<?> page){
            this.number = page.getNumber();
            this.currentPageNumber = page.getNumber() + 1;
            this.numberOfPages = page.getTotalPages();
            this.totalElements = page.getTotalElements();
            this.hasPreviousPage = page.hasPrevious();
            this.hasNextPage = page.hasNext();
            this.previousPageNumber = hasPreviousPage ? page.getNumber() : null;
            this.nextPageNumber = hasNextPage ? page.getNumber() + 2 : null;
        }
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

    public BaseResponse(Boolean success, Integer code, String message, Page<T> data){
        this.response = new Response(success,code,message);
        this.dataList = data.getContent();
        new ResponseMetadata(data);
    }

    /**
     * Helper Responses 
     * @author JohnMalugu
     */

    public static <T> BaseResponse<T> error(Integer code,String message) {
        return new BaseResponse<>(false, code, message);
    }


    public static <T> BaseResponse<T> error(Integer code, String message, T data) {
        BaseResponse<T> response = new BaseResponse<>(false, code, message);
        response.setData(data);
        return response;
    }


    public static <T> BaseResponse<T> exception(Exception e) {
        log.error("An exception occurred while processing request: ", e);
        return new BaseResponse<>(false, ResponseCode.EXCEPTION, "An exception occurred while processing request");
    }
    
}
