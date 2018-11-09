package io.github.rxcats.springbootwebsocketdemo.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    ok(0, "Success"),
    error(900001, "Service Error"),
    invalid_parameter(900002, "Invalid Parameter"),

    ;

    final int code;
    final String message;

}
