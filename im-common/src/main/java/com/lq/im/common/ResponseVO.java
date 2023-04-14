package com.lq.im.common;

import com.lq.im.common.exception.ApplicationExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ResponseVO
 * @Author: LiQi
 * @Date: 2023-04-11 11:27
 * @Version: V1.0
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO<T> {

    private int code;

    private String msg;

    private T data;

    public static <U> ResponseVO<U> successResponse(U data) {
        return new ResponseVO<> (200, "success", data);
    }

    public static ResponseVO successResponse() {
        return new ResponseVO(200, "success");
    }

    public static ResponseVO errorResponse() {
        return new ResponseVO(500, "系统内部异常");
    }

    public static ResponseVO errorResponse(int code, String msg) {
        return new ResponseVO(code, msg);
    }

    public static ResponseVO errorResponse(ApplicationExceptionEnum enums) {
        return new ResponseVO(enums.getCode(), enums.getError());
    }

    public boolean isOk(){
        return this.code == 200;
    }


    public ResponseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseVO success(){
        this.code = 200;
        this.msg = "success";
        return this;
    }

    public ResponseVO<T> success(T data){
        this.code = 200;
        this.msg = "success";
        this.data = data;
        return this;
    }

}
