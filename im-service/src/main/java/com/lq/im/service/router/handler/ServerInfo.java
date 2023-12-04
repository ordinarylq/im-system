package com.lq.im.service.router.handler;

import com.lq.im.common.exception.ApplicationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.lq.im.common.BaseErrorCodeEnum.PARAMETER_ERROR;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerInfo {
    private String ip;
    private Integer port;

    public static ServerInfo parse(String server) {
        try {
            String[] serverHost = server.split(":");
            return new ServerInfo(serverHost[0], Integer.valueOf(serverHost[1]));
        } catch (Exception e) {
            throw new ApplicationException(PARAMETER_ERROR);
        }
    }
}
