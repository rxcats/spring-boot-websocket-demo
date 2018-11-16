package io.github.rxcats.springbootwebsocketdemo.message;

import lombok.Data;

@Data
public class ServerMessage {

    String from;

    String message;

    String topic;

    long time = System.currentTimeMillis();

}
