# Java Multi-Mode Server (Echo & Greeting)

A high-performance, multi-threaded TCP Server built in Java using the **Strategy Pattern**. The server listens for incoming TCP socket connections and handles multiple clients concurrently using a cached thread pool (`ExecutorService`). 

It supports multiple operation modes via command-line arguments:
1. **Echo Mode** (`--mode echo`): Echoes back any received line of text.
2. **Greeting Mode** (`--mode greeting`): Provides an interactive greeting protocol supporting `NAME`, `LOCATION`, `GREET`, and `QUIT` commands with client timeout controls.

---

## Features

- **Strategy Pattern Architecture**: Core server lifecycle (`EchoServer`) is decoupled from protocol handlers (`EchoHandler`, `GreetingHandler`).
- **Concurrent TCP Server**: Handles multiple client connections simultaneously using a cached thread pool (`ExecutorService`).
- **CLI Parameter Parsing**: Configurable CLI parameters powered by **JCommander**:
  - `-p`, `--port`: Port number to listen on (Default: `1234`)
  - `-m`, `--mode`: Server mode (`echo` or `greeting`, Default: `echo`)
  - `-t`, `--timeout`: Client connection timeout in seconds for greeting mode (Default: `45`)
  - `-h`, `--help`: Display CLI usage help
- **Graceful Shutdown**: Registers a JVM shutdown hook to close listening sockets and clean up thread pools gracefully.
- **Logging**: SLF4J logger integration for operational insights.
- **Unit & Integration Tests**: Built with **JUnit 5** and **Mockito**, featuring end-to-end socket testing for both server modes.
- **Fat JAR Packaging**: Assembles executable standalone JARs via Gradle.

---

## Prerequisites

- **Java Development Kit (JDK)** 21 or higher
- **Gradle** (or use the included `./gradlew` wrapper)

---

## Building and Running

### 1. Build the Project & Fat JAR
```bash
./gradlew build
```

### 2. Run the Server

#### Echo Mode (Default)
```bash
./gradlew run --args="--mode echo --port 1234"
```

#### Greeting Mode
```bash
./gradlew run --args="--mode greeting --port 6666 --timeout 30"
```

#### Display CLI Options
```bash
./gradlew run --args="--help"
```

#### Executable Fat JAR
```bash
java -jar build/libs/echo-server-1.0-SNAPSHOT.jar --mode greeting -p 6666
```

---

## Protocol Specifications

### Greeting Protocol (`--mode greeting`)
Upon connection, the server sends `200 server ready`. Supported commands:

| Command | Example | Response |
| :--- | :--- | :--- |
| `NAME <name>` | `NAME Alice` | `201 NAME ok` |
| `LOCATION <location>` | `LOCATION Wonderland` | `201 LOCATION ok` |
| `GREET` | `GREET` | `Hello Alice of Wonderland` |
| `QUIT` | `QUIT` | `202 Bye` |

---

## Testing

### Automated Unit & Integration Tests
Run the test suite using Gradle:
```bash
./gradlew test
```

### Manual Testing with Netcat (`nc`)

#### Test Echo Server
```bash
nc localhost 1234
```
Type any message and press **Enter** to see it echoed back.

#### Test Greeting Server
```bash
nc localhost 6666
```
Sample Session:
```text
200 server ready
NAME Alice
201 NAME ok
LOCATION Wonderland
201 LOCATION ok
GREET
Hello Alice of Wonderland
QUIT
202 Bye
```

---

## License

This project is licensed under the [MIT License](LICENSE).
