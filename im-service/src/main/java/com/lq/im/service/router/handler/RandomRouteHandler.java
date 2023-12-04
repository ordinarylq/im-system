package com.lq.im.service.router.handler;


import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class RandomRouteHandler extends RouteHandler{
    @Override
    public String doChoose(List<String> serverList, String key) {
        int size = serverList.size();
        int index = ThreadLocalRandom.current().nextInt(size);
        return serverList.get(index);
    }
}
