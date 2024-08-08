package com.example.KavaSpring.services;

public interface WebSocketService {

    void addUser(String username);
    void removeUser(String username);
    void broadcastConnectedUsers();
}
