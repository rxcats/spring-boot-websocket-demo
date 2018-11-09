package io.github.rxcats.springbootwebsocketdemo.ws;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class WsMessageEntity {

    String cmd;

    Map<String, Object> headers;

    byte[] body;

}
