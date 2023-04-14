package com.lq.im.common.exception;

/**
 * @ClassName: ApplicationException
 * @Author: LiQi
 * @Date: 2023-04-11 11:18
 * @Version: V1.0
 * @Description:
 */
public class ApplicationException extends RuntimeException {
    private int code;

    private String error;

    public ApplicationException(int code, String message) {
        super(message);
        this.code = code;
        this.error = message;
    }

    public ApplicationException(ApplicationExceptionEnum exceptionEnum) {
        super(exceptionEnum.getError());
        this.code   = exceptionEnum.getCode();
        this.error  = exceptionEnum.getError();
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }


    /**
     *  avoid the expensive and useless stack trace for api exceptions
     *  @see Throwable#fillInStackTrace()
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
