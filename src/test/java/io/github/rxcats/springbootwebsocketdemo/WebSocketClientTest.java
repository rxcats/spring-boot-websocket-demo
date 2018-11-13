package io.github.rxcats.springbootwebsocketdemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebSocketClientTest {

    @LocalServerPort
    int port;

    @MockBean
    ServletServerContainerFactoryBean servletServerContainerFactoryBean;

    @Test
    public void test() {
        WebSocketClient client = new WebSocketClient();

        client.connect("ws://localhost:" + port + "/api");

        String json = "{\"cmd\":\"/hello/world\",\"headers\":{\"type\":1},\"body\":{\"id\":1}}";

        String response = client.syncSendMessage(json);

        log.info("cli response : {}", response);

        client.disconnect();

    }
}
