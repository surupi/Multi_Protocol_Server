package org.example;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.handler.ClientHandler;
import org.example.handler.EchoHandler;
import org.example.handler.GreetingHandler;

@Getter
@Slf4j
public class EchoServerApplication {
    @Parameter(names = {"--port", "-p"}, description = "Port number for the Server to listen on")
    private int port = 1234;

    @Parameter(names = {"--mode", "-m"}, description = "Server mode: echo or greeting (default: echo)")
    private String mode = "echo";

    @Parameter(names = {"--timeout", "-t"}, description = "Client timeout in seconds for greeting mode")
    private int timeout = 45;

    @Parameter(names = {"--help", "-h"}, help = true, description = "Display CLI help message")
    private boolean help = false;

    public static void main(String[] args) {
        EchoServerApplication app = new EchoServerApplication();
        JCommander commander = JCommander.newBuilder()
                .addObject(app)
                .build();

        commander.parse(args);

        if (app.isHelp()) {
            commander.usage();
            return;
        }

        app.run();
    }

    public void run() {
        ClientHandler handler;
        if ("greeting".equalsIgnoreCase(mode)) {
            log.info("Running in Greeting mode");
            handler = new GreetingHandler(timeout);
        } else {
            log.info("Running in Echo mode");
            handler = new EchoHandler();
        }

        EchoServer server = new EchoServer(this.port, handler);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
    }
}