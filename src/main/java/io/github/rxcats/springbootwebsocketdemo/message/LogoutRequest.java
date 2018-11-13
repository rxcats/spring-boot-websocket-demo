package io.github.rxcats.springbootwebsocketdemo.message;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LogoutRequest {

    @NotBlank
    String userId;

}
