package io.github.rxcats.springbootwebsocketdemo.message;

import javax.validation.constraints.Min;

import lombok.Data;

@Data
public class WorldRequest {

    @Min(2)
    long id;

}
