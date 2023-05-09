package de.qaware.ec2023.bciw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@ServerEndpoint(value = "/counterSocket", encoders = CounterWebSocket.CounterEncoder.class)
public class CounterWebSocket {
    List<Session> sessions = Collections.synchronizedList(new ArrayList<>());

    @Inject
    CounterService counterService;

    @PostConstruct
    public void postConstruct() {
        counterService.addListener(this::notifyListeners);
    }

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        notifyListener(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
        log.error("OnError: ", throwable);
    }

    @OnMessage
    public void onMessage(String message) {
        switch (message) {
            case "increment" -> this.counterService.increment();
            case "decrement" -> this.counterService.decrement();
            default -> log.warn("warn " + message);
        }
    }

    private void notifyListeners() {
        sessions.forEach(s -> notifyListener(s));
    }

    private void notifyListener(Session s) {
        CounterValue message = new CounterValue(counterService.getCounter());
        s.getAsyncRemote().sendObject(message, result -> {
            if (result.getException() != null) {
                log.error("Could an exception: ", result.getException());
            }
        });
    }


    public static class CounterEncoder implements Encoder.Text<CounterValue> {
        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public String encode(CounterValue object) {
            try {
                return mapper.writeValueAsString(object);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
