package com.lq.im.service.exception;

import com.lq.im.common.BaseErrorCodeEnum;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.exception.ApplicationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * 全局异常处理
 * @ClassName: GlobalExceptionHandler
 * @Author: LiQi
 * @Date: 2023-04-11 14:36
 * @Version: V1.0
 * @Description:
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseVO missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        return new ResponseVO(
                BaseErrorCodeEnum.PARAMETER_ERROR.getCode(),
                BaseErrorCodeEnum.PARAMETER_ERROR.getError() + " : " + e.getMessage());
    }



    /**
     * 参数校验异常
     * @author LiQi
     * @param  e
     * @return Object
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseBody
    public ResponseVO constraintViolationExceptionHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        ResponseVO responseVO = new ResponseVO();
        responseVO.setCode(BaseErrorCodeEnum.PARAMETER_ERROR.getCode());

        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            PathImpl propertyPath = (PathImpl) constraintViolation.getPropertyPath();
            String paramName = propertyPath.getLeafNode().getName();
            sb.append("参数{").append(paramName).append("}").append(constraintViolation.getMessage()).append("\n");
        }
        if(sb.length() > 0) {
            responseVO.setMsg(sb.toString());
        } else {
            responseVO.setMsg(BaseErrorCodeEnum.PARAMETER_ERROR.getError() + e.getMessage());
        }
        return responseVO;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseVO methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        StringBuilder sb = new StringBuilder();
        BindingResult bindingResult = e.getBindingResult();
        for (ObjectError allError : bindingResult.getAllErrors()) {
            sb.append(allError.getDefaultMessage()).append(",");
        }
        sb.delete(sb.length() - 1, sb.length());

        return new ResponseVO(BaseErrorCodeEnum.PARAMETER_ERROR.getCode(),
                BaseErrorCodeEnum.PARAMETER_ERROR.getError() + " : " + sb);
    }

    /**
     * 自定义异常处理
     * @author LiQi
     * @param e
     * @return ResponseVO
     */
    @ExceptionHandler(ApplicationException.class)
    @ResponseBody
    public ResponseVO applicationExceptionHandler(ApplicationException e) {
        ResponseVO responseVO = new ResponseVO(e.getCode(), e.getError());
        return responseVO;
    }

    /**
     * 参数绑定异常
     * @author LiQi
     * @param e
     * @return ResponseVO
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseVO bindExceptionHandler(BindException e) {
        FieldError fieldError = e.getFieldError();
        String message = "参数{" + fieldError.getField() + "}" + fieldError.getDefaultMessage();
        return new ResponseVO(BaseErrorCodeEnum.PARAMETER_ERROR.getCode(), message);
    }

    /**
     * 未知异常处理
     * @author LiQi
     * @param e
     * @return ResponseVO
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseVO unknownExceptionHandler(Exception e) {
        e.printStackTrace();
        ResponseVO responseVO = ResponseVO.errorResponse(BaseErrorCodeEnum.INTERNAL_ERROR);
        responseVO.setMsg(responseVO.getMsg() + ": " + e.getMessage());
        // todo 出现异常时的通知处理逻辑，发邮件、短信等
        return responseVO;
    }

}
