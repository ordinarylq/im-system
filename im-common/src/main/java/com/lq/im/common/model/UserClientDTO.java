package com.lq.im.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserClientDTO {
    private Integer appId;

    private Integer clientType;

    private String userId;

    private String imei;
}
