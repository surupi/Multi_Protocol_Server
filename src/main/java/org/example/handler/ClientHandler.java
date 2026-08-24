package org.example.handler;

import java.net.Socket;

@FunctionalInterface
public interface ClientHandler {
    void handle(Socket clientSocket);
}
