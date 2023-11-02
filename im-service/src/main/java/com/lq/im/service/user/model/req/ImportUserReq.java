package com.lq.im.service.user.model.req;

import com.lq.im.common.model.RequestBase;
import com.lq.im.service.user.model.ImUserDAO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

import static com.lq.im.common.exception.ApplicationExceptionEnum.REQUEST_DATA_DOES_NOT_EXIST_MESSAGE;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportUserReq extends RequestBase {

    @NotEmpty(message = REQUEST_DATA_DOES_NOT_EXIST_MESSAGE)
    private List<ImUserDAO> userList;
}
