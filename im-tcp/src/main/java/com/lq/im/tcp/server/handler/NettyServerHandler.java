package com.lq.im.tcp.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.lq.im.codec.body.ChatMessageAck;
import com.lq.im.codec.proto.ImServiceMessage;
import com.lq.im.codec.proto.Message;
import com.lq.im.codec.proto.MessageHeader;
import com.lq.im.common.ResponseVO;
import com.lq.im.common.constant.Constants;
import com.lq.im.common.enums.gateway.SystemCommand;
import com.lq.im.common.enums.message.MessageCommand;
import com.lq.im.common.model.message.GroupMessageContent;
import com.lq.im.common.model.message.MessageContent;
import com.lq.im.common.model.message.SendGroupMessageCheckReq;
import com.lq.im.common.model.message.SendMessageCheckReq;
import com.lq.im.tcp.feign.MessageService;
import com.lq.im.tcp.mq.producer.MessageProducer;
import com.lq.im.tcp.utils.SessionHandler;
import feign.Feign;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Slf4j
@SuppressWarnings("rawtypes,unchecked")
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    private Integer brokerId;

    private MessageService messageService;

    public NettyServerHandler(Integer brokerId, String logicUrl) {
        this.brokerId = brokerId;
        this.messageService = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .options(new Request.Options(1000, 3500))
                .target(MessageService.class, logicUrl);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        MessageHeader header = msg.getHeader();
        Integer command = header.getCommand();
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(msg.getBody()));
        if (command == SystemCommand.LOGIN.getCommand()) {
            SessionHandler.login((NioSocketChannel) ctx.channel(), msg, brokerId);
        } else if (command == SystemCommand.LOGOUT.getCommand()) {
            SessionHandler.logout((NioSocketChannel) ctx.channel());
        } else if (command == SystemCommand.PING.getCommand()) {
            ctx.channel().attr(AttributeKey.valueOf(Constants.LAST_READ_TIME)).set(System.currentTimeMillis());
        } else if (command == MessageCommand.PEER_TO_PEER.getCommand()) {
            MessageContent messageContent = jsonObject.toJavaObject(MessageContent.class);
            ResponseVO resp = checkP2PMessageBeforeSend(header, messageContent);
            if (resp.isOk()) {
                MessageProducer.sendMessage(msg);
            } else {
                // ack to sender
                ImServiceMessage<ResponseVO<?>> serviceMessage = getP2PMessageResp(resp, messageContent);
                log.info("msg ack, msgId={}, checkResult={}", messageContent.getMessageId(), serviceMessage);
                ctx.channel().writeAndFlush(serviceMessage);
            }
        } else if (command == MessageCommand.PEER_TO_GROUP.getCommand()) {
            GroupMessageContent groupMessageContent = jsonObject.toJavaObject(GroupMessageContent.class);
            ResponseVO resp = checkGroupMessageBeforeSend(header, groupMessageContent);
            if (resp.isOk()) {
                MessageProducer.sendMessage(msg);
            } else {
                // ack to sender
                ImServiceMessage<ResponseVO<?>> serviceMessage = getGroupMessageResp(resp, groupMessageContent);
                log.info("group msg ack, msgId={}, checkResult={}", groupMessageContent.getMessageId(), serviceMessage);
                ctx.channel().writeAndFlush(serviceMessage);
            }
        } else {
            MessageProducer.sendMessage(msg);
        }
    }

    private ResponseVO<?> checkP2PMessageBeforeSend(MessageHeader header, MessageContent messageContent) {
        SendMessageCheckReq req = getP2PMessageCheckRequest(header, messageContent);
        return this.messageService.checkPeerToPeerMessage(req);
    }

    /**
     * 创建单聊消息检查请求对象
     */
    private SendMessageCheckReq getP2PMessageCheckRequest(MessageHeader header, MessageContent messageContent) {
        SendMessageCheckReq req = new SendMessageCheckReq();
        req.setAppId(header.getAppId());
        req.setCommand(header.getCommand());
        req.setUserId(messageContent.getUserClient().getUserId());
        req.setFriendUserId(messageContent.getFriendUserId());
        return req;
    }

    private ImServiceMessage<ResponseVO<?>> getP2PMessageResp(ResponseVO resp, MessageContent messageContent) {
        ImServiceMessage<ResponseVO<?>> serviceMessage = getBasicMessage(resp, messageContent);
        serviceMessage.setCommand(MessageCommand.MESSAGE_ACK.getCommand());
        return serviceMessage;
    }

    private ImServiceMessage<ResponseVO<?>> getBasicMessage(ResponseVO resp, MessageContent messageContent) {
        ImServiceMessage<ResponseVO<?>> serviceMessage = new ImServiceMessage<>();
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
        resp.setData(chatMessageAck);
        serviceMessage.setData(resp);
        BeanUtils.copyProperties(messageContent.getUserClient(), serviceMessage);
        serviceMessage.setMessageId(messageContent.getMessageId());
        return serviceMessage;
    }

    private ResponseVO<?> checkGroupMessageBeforeSend(MessageHeader header, GroupMessageContent groupMessageContent) {
        SendGroupMessageCheckReq req = getGroupCheckRequest(header, groupMessageContent);
        return this.messageService.checkGroupMessage(req);
    }

    /**
     * 创建群组消息检查请求对象
     */
    private SendGroupMessageCheckReq getGroupCheckRequest(MessageHeader header, GroupMessageContent groupMessageContent) {
        SendGroupMessageCheckReq req = new SendGroupMessageCheckReq();
        req.setAppId(header.getAppId());
        req.setCommand(header.getCommand());
        req.setUserId(groupMessageContent.getUserClient().getUserId());
        req.setGroupId(groupMessageContent.getGroupId());
        return req;
    }

    private ImServiceMessage<ResponseVO<?>> getGroupMessageResp(ResponseVO resp, GroupMessageContent groupMessageContent) {
        ImServiceMessage<ResponseVO<?>> serviceMessage = getBasicMessage(resp, groupMessageContent);
        serviceMessage.setCommand(MessageCommand.GROUP_MESSAGE_ACK.getCommand());
        return serviceMessage;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SessionHandler.offline((NioSocketChannel) ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("An error occurred: {}", cause.getMessage());
        log.error("Removing problematic channel");
        SessionHandler.logout((NioSocketChannel) ctx.channel());
    }
}
