package com.lq.im.tcp.server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyMessage {
    private String from;
    private String to;
    private String message;
}
