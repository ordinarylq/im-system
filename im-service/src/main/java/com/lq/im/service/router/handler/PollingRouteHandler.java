package com.lq.im.service.router.handler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PollingRouteHandler extends RouteHandler{
    private static AtomicInteger index = new AtomicInteger();

    @Override
    public String doChoose(List<String> serverList, String key) {
        int result = index.getAndIncrement() % serverList.size();
        if (result < 0) {
            result = 0;
        }
        return serverList.get(result);
    }
}
