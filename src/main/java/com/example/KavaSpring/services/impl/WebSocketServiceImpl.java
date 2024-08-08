package com.example.KavaSpring.services.impl;

import com.example.KavaSpring.services.WebSocketService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class WebSocketServiceImpl implements WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    private final Set<String> connectedUsers = new HashSet<>();

    @Override
    public void addUser(String username) {
        connectedUsers.add(username);
        broadcastConnectedUsers();
    }

    @Override
    public void removeUser(String username) {
        connectedUsers.remove(username);
        broadcastConnectedUsers();
    }

    @Override
    public void broadcastConnectedUsers() {
        messagingTemplate.convertAndSend("topic/connected-users", new ArrayList<>(connectedUsers));
    }
}
