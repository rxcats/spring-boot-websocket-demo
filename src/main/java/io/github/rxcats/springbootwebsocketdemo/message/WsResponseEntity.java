package io.github.rxcats.springbootwebsocketdemo.message;

import lombok.Data;

@Data
public class WsResponseEntity<T> {

    String cmd = "";

    int code = ResultCode.ok.code;

    T result;

    String message = "";

    String details = "";

    long timestamp = System.currentTimeMillis();

    private WsResponseEntity() {

    }

    private WsResponseEntity(String cmd, T result) {
        this();
        this.cmd = cmd;
        this.result = result;
    }

    private WsResponseEntity(ResultCode code, String cmd, T result) {
        this();
        this.code = code.code;
        this.cmd = cmd;
        this.result = result;
    }

    private WsResponseEntity(ResultCode code, String cmd, T result, String details) {
        this(code, cmd, result);
        this.details = details;
    }

    public static <T> WsResponseEntity<T> of(String cmd, T result) {
        return new WsResponseEntity<>(cmd, result);
    }

    public static <T> WsResponseEntity<T> of(ResultCode code, String cmd, T result) {
        return new WsResponseEntity<>(code, cmd, result);
    }

    public static <T> WsResponseEntity<T> of(ResultCode code, String cmd, T result, String details) {
        return new WsResponseEntity<>(code, cmd, result, details);
    }

    public static <T> WsResponseEntity<T> error(String cmd, String details) {
        return new WsResponseEntity<>(ResultCode.error, cmd, null, details);
    }

}
