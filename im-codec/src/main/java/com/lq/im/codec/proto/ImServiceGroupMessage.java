package com.lq.im.codec.proto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ImServiceGroupMessage<T> extends ImServiceMessage<T> {

    private String groupId;

}
