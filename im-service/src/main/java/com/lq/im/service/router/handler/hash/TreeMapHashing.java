package com.lq.im.service.router.handler.hash;

import java.util.TreeMap;


public class TreeMapHashing extends AbstractHashing {
    private static final int VIRTUAL_NODE_SIZE = 2;
    private static final String VIRTUAL_NODE_PREFIX = "NODE";
    private TreeMap<Long, String> map = new TreeMap<>();

    @Override
    protected void add(long key, String value) {
        // avoid some particular node that got selected all the time
        // caused by unbalanced hash value.
        for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
            map.put(super.hash(VIRTUAL_NODE_PREFIX + key + i), value);
        }
        map.put(key, value);
    }

    @Override
    protected String getNode(String userId) {
        long hash = super.hash(userId);
        Long resultKey = map.ceilingKey(hash);
        if (resultKey != null) {
            return map.get(resultKey);
        }
        return map.firstEntry().getValue();
    }

    @Override
    protected void beforeProcess() {
        map.clear();
    }
}
