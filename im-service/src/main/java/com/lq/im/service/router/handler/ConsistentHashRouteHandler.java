package com.lq.im.service.router.handler;

import com.lq.im.service.router.handler.hash.AbstractHashing;

import java.util.List;

public class ConsistentHashRouteHandler extends RouteHandler {
    private AbstractHashing hashing;

    public void setHashing(AbstractHashing hashing) {
        this.hashing = hashing;
    }

    @Override
    public String doChoose(List<String> serverList, String key) {
        return this.hashing.process(serverList, key);
    }
}
