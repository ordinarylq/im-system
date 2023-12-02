package com.lq.im.tcp.redis;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.codec.body.OfflineNotificationMessageBody;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.enums.gateway.LoginClientType;
import com.lq.im.common.enums.gateway.LoginDeviceType;
import com.lq.im.common.enums.gateway.SystemCommand;
import com.lq.im.common.model.UserClientDTO;
import com.lq.im.tcp.utils.SessionHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.listener.MessageListener;

import java.util.List;

/**
 * 多端同步消息监听
 */
@Slf4j
public class RedisChannelListener implements MessageListener<String> {
    private Integer loginMode;

    public RedisChannelListener(Integer loginMode) {
        this.loginMode = loginMode;
    }

    @Override
    public void onMessage(CharSequence charSequence, String msg) {
        UserClientDTO userClientDTO = JSONObject.parseObject(msg, UserClientDTO.class);
        log.info("接收到{}应用的用户{}的{}设备上线通知",
                userClientDTO.getAppId(), userClientDTO.getUserId(), userClientDTO.getImei());
        String newClient = userClientDTO.getClientType() + ":" + userClientDTO.getImei();
        List<NioSocketChannel> userRelatedChannelList = SessionHandler.getUserRelatedChannelList(userClientDTO);
        for (NioSocketChannel channel : userRelatedChannelList) {
            Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
            if (userClientDTO.getClientType() == LoginDeviceType.WEB.getCode()
                    || clientType == LoginDeviceType.WEB.getCode()) {
                continue;
            }
            String imei = (String) channel.attr(AttributeKey.valueOf(Constants.DEVICE_IMEI)).get();
            String client = clientType + ":" + imei;
            if (loginMode == LoginClientType.ONE.getCode()) {
                if (!newClient.equals(client)) {
                    notifySameClientLogin(channel);
                }
            } else if (loginMode == LoginClientType.TWO.getCode()) {
                if (!newClient.equals(client)) {
                    notifySameClientLogin(channel);
                }
            } else if (loginMode == LoginClientType.THREE.getCode()) {
                boolean isSameClientType = isSameClientType(userClientDTO.getClientType(), clientType);
                if (isSameClientType && !newClient.equals(client)) {
                    notifySameClientLogin(channel);
                }
            }
        }
    }

    private boolean isSameClientType(Integer clientType, Integer anotherClientType) {
        return (isMobileClientType(clientType) && isMobileClientType(anotherClientType))
                || (isPcClientType(clientType) && isPcClientType(anotherClientType));
    }

    private boolean isMobileClientType(Integer clientType) {
        return clientType == LoginDeviceType.ANDROID.getCode() || clientType == LoginDeviceType.IPHONE.getCode() ||
                clientType == LoginDeviceType.IPAD.getCode();
    }

    private boolean isPcClientType(Integer clientType) {
        return clientType == LoginDeviceType.WINDOWS.getCode() || clientType == LoginDeviceType.MAC.getCode();
    }

    private void notifySameClientLogin(NioSocketChannel channel) {
        OfflineNotificationMessageBody<Object> msg = new OfflineNotificationMessageBody<>();
        msg.setCommand(SystemCommand.OFFLINE_NOTIFICATION.getCommand());
        String userId = (String) channel.attr(AttributeKey.valueOf(Constants.USER_ID)).get();
        msg.setReceiverId(userId);
        msg.setUserId(userId);
        channel.writeAndFlush(msg);
    }
}
