package com.lq.im.common;

import com.lq.im.common.exception.ApplicationExceptionEnum;
import static com.lq.im.common.exception.ApplicationExceptionEnum.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO<T> {

    private int code;

    private String msg;

    private T data;

    public static <U> ResponseVO<U> successResponse(U data) {
        return new ResponseVO<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    public static <U> ResponseVO<U> successResponse() {
        return new ResponseVO<>(SUCCESS_CODE, SUCCESS_MESSAGE);
    }

    public static <U> ResponseVO<U> errorResponse() {
        return new ResponseVO<>(INTERNAL_ERROR_CODE, INTERNAL_ERROR_MESSAGE);
    }

    public static <U> ResponseVO<U> errorResponse(int code, String msg) {
        return new ResponseVO<>(code, msg);
    }

    public static <U> ResponseVO<U> errorResponse(ApplicationExceptionEnum enums) {
        return new ResponseVO<>(enums.getCode(), enums.getError());
    }

    public boolean isOk(){
        return this.code != SUCCESS_CODE;
    }

    public ResponseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseVO<?> success(){
        this.code = SUCCESS_CODE;
        this.msg = SUCCESS_MESSAGE;
        return this;
    }

    public ResponseVO<T> success(T data){
        this.code = SUCCESS_CODE;
        this.msg = SUCCESS_MESSAGE;
        this.data = data;
        return this;
    }
}
