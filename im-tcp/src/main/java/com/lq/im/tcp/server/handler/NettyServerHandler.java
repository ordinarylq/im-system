package com.lq.im.tcp.server.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lq.im.codec.pack.LoginPack;
import com.lq.im.codec.proto.Message;
import com.lq.im.codec.proto.MessageHeader;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.enums.ImConnecStatusEnum;
import com.lq.im.common.enums.SystemCommand;
import com.lq.im.common.model.UserSession;
import com.lq.im.tcp.redis.RedisManager;
import com.lq.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        MessageHeader header = msg.getHeader();
        Integer command = header.getCommand();
        if (command == SystemCommand.LOGIN.getCommand()) {
            LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(msg.getBody()),
                    new TypeReference<LoginPack>() {}.getType());
            // set channel attributes
            ctx.channel().attr(AttributeKey.valueOf(Constants.APP_ID)).set(header.getAppId());
            ctx.channel().attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).set(header.getClientType());
            ctx.channel().attr(AttributeKey.valueOf(Constants.USER_ID)).set(loginPack.getUserId());
            // save session to Redis
            UserSession userSession = new UserSession(loginPack.getUserId(), header.getAppId(), header.getClientType(), header.getVersion(),
                    ImConnecStatusEnum.ONLINE_STATUS.getCode());
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            String hashKey = header.getAppId() + Constants.RedisConstants.USER_SESSION + loginPack.getUserId();
            RMap<String, String> map = redissonClient.getMap(hashKey);
            map.put(String.valueOf(header.getClientType()), JSONObject.toJSONString(userSession));
            // save session-channel to a holder
            SessionSocketHolder.put(header.getAppId(), header.getClientType(), loginPack.getUserId(),
                    (NioSocketChannel) ctx.channel());
        } else if (command == SystemCommand.LOGOUT.getCommand()) {
            // get channel attributes
            Integer appId = (Integer) ctx.channel().attr(AttributeKey.valueOf(Constants.APP_ID)).get();
            Integer clientType = (Integer) ctx.channel().attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
            String userId = (String) ctx.channel().attr(AttributeKey.valueOf(Constants.USER_ID)).get();
            SessionSocketHolder.remove(appId, clientType, userId);
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            String hashKey = appId + Constants.RedisConstants.USER_SESSION + userId;
            redissonClient.getMap(hashKey).remove(clientType);
            ctx.channel().close();
        }

    }
}
