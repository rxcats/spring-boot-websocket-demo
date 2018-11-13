package io.github.rxcats.springbootwebsocketdemo;

import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

@ClientEndpoint
public class WebSocketClient {

    private WebSocketContainer container;
    private Session userSession;
    private Object lock;
    private String response;

    public WebSocketClient() {
        container = ContainerProvider.getWebSocketContainer();
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("onOpen:" + session.getId());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("onClose:" + session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String msg) {
        try {
            response = msg;
            synchronized (lock) {
                lock.notify();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect(String serverUri) {
        try {
            userSession = container.connectToServer(this, new URI(serverUri));
            userSession.setMaxTextMessageBufferSize(8192);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String syncSendMessage(String msg) {
        try {
            long startTime = System.currentTimeMillis();
            lock = new Object();
            userSession.getBasicRemote().sendText(msg);
            synchronized (lock) {
                lock.wait();
            }

            System.out.println("estimatedTime:" + (System.currentTimeMillis() - startTime));

            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void disconnect() {
        try {
            if (userSession != null && userSession.isOpen()) {
                userSession.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
