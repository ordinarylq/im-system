package com.lq.im.tcp.mq.processor;


public class MessageProcessFactory {

    private static BaseProcessor processor = new DefaultMessageProcessor();

    public static BaseProcessor getMessageProcessor(Integer command) {
        return processor;
    }

}
