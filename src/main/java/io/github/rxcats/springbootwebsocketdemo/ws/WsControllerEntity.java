package io.github.rxcats.springbootwebsocketdemo.ws;

import java.lang.reflect.Method;

import lombok.Data;

@Data
public class WsControllerEntity {

    private Object bean;

    private Method method;

    public WsControllerEntity(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    public static WsControllerEntity of(Object bean, Method method) {
        return new WsControllerEntity(bean, method);
    }

}
