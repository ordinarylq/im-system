package com.lq.im.common.exception;

public interface ApplicationExceptionEnum {

    int SUCCESS_CODE = 200;
    String SUCCESS_MESSAGE = "success";

    int INTERNAL_ERROR_CODE = 500;
    String INTERNAL_ERROR_MESSAGE = "Internal Error, please contact Administrator.";

    int TOO_MUCH_DATA_CODE = 20000;
    String TOO_MUCH_DATA_MESSAGE = "导入数量超出上限";

    int USER_IS_NOT_EXIST_CODE = 20001;
    String USER_IS_NOT_EXIST_MESSAGE = "用户不存在";

    int SERVER_GET_USER_ERROR_CODE = 20002;
    String SERVER_GET_USER_ERROR_MESSAGE = "服务获取用户失败";

    int MODIFY_USER_ERROR_CODE = 20003;
    String MODIFY_USER_ERROR_MESSAGE = "更新用户失败";

    int REQUEST_DATA_DOES_NOT_EXIST_CODE = 20004;
    String REQUEST_DATA_DOES_NOT_EXIST_MESSAGE = "请求数据不存在";

    int SERVER_NOT_AVAILABLE_CODE = 71000;
    String SERVER_NOT_AVAILABLE_MESSAGE = "没有可用的服务";

    int PARAMETER_VALIDATION_ERROR_CODE = 90001;
    String PARAMETER_VALIDATION_ERROR_MESSAGE = "Parameter validation error";

    int getCode();

    String getError();
}
