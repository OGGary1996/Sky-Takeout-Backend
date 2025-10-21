package com.sky.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketServer extends TextWebSocketHandler {
    // 1. 保存所有连接
    private static final ConcurrentHashMap<String, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();

    // 2. 连接建立之后回调
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        SESSION_MAP.put(sessionId, session);
        log.info("Connected to websocket session {}", sessionId);
    }

    // 3. 关闭连接之后回调
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        SESSION_MAP.remove(session.getId());
        log.info("Disconnected from websocket session {}", session.getId());
    }

    /*
    * 核心方法，业务逻辑中调用
    * 调用位置，微信支付成功回调 -> OrderServiceImpl.paySuccess中
    * 逻辑：
    *  1. 遍历SESSION_MAP中的所有session
    *  2. 调用session.sendMessage()发送消息
    *  3. 消息格式转换：先创建Map<String,String> 然后Map转JSON
    *  4. 本方法接收转换之后的JSON直接发送
    * */
    public void sendMessage(String message) {
        Map<String, String> messageMap = new HashMap<>();
        SESSION_MAP.entrySet().stream().forEach(entry -> {
            WebSocketSession session = entry.getValue();
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
