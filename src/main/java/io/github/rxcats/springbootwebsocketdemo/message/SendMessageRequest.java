package io.github.rxcats.springbootwebsocketdemo.message;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SendMessageRequest {

    @NotBlank
    String userId;

    @NotBlank
    String message;
}
