package io.github.rxcats.springbootwebsocketdemo.message;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class WorldRequest {

    long id;

}
