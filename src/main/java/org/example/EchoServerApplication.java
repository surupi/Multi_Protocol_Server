package org.example;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class EchoServerApplication {
    @Parameter(names = {"--port", "-p"}, description = "Port number for the Echo Server to listen on")
    private int port = 1234;

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
        EchoServer server = new EchoServer(this.port);
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
    }
}