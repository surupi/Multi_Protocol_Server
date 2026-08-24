package org.example.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.model.GreetingCommand;
import org.example.util.ParseUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@Slf4j
public class GreetingHandler implements ClientHandler {
    private final int timeout;

    public GreetingHandler() {
        this.timeout = 45;
    }

    public GreetingHandler(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public void handle(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            if (timeout > 0) {
                clientSocket.setSoTimeout(timeout * 1000);
            }
            out.println("200 server ready");
            String line;
            String name = "";
            String location = "";

            while ((line = in.readLine()) != null) {
                GreetingCommand cmd = ParseUtil.parseInput(line);

                switch (cmd.getName().toUpperCase()) {
                    case "NAME":
                        name = String.join(" ", cmd.getArguments());
                        out.println("201 NAME ok");
                        break;

                    case "LOCATION":
                        location = String.join(" ", cmd.getArguments());
                        out.println("201 LOCATION ok");
                        break;

                    case "GREET":
                        if (name.isEmpty() || location.isEmpty()) {
                            out.println("400 Bad Request");
                        } else {
                            out.printf("Hello %s of %s%n", name, location);
                        }
                        break;

                    case "QUIT":
                        out.println("202 Bye");
                        return;

                    default:
                        out.println("400 Bad Request");
                        break;
                }
            }
        } catch (IOException e) {
            log.debug("Greeting client connection ended: {}", e.getMessage());
        } finally {
            try {
                clientSocket.close();
                log.info("Client connection closed: {}", clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                log.error("Error closing client socket", e);
            }
        }
    }
}
