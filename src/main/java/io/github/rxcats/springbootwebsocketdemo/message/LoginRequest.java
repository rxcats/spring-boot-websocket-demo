package io.github.rxcats.springbootwebsocketdemo.message;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    String userId;

}
