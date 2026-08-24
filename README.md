# Java Echo Server

A high-performance, multi-threaded TCP Echo Server built in Java. The server listens for incoming TCP connections, handles multiple client connections concurrently using a thread pool, and echoes back any received messages line-by-line.

## Features

- **Concurrent TCP Server**: Handles multiple client connections simultaneously using a cached thread pool (`ExecutorService`).
- **CLI Argument Parsing**: Easily configurable server parameters (e.g., `-p` / `--port`, `-h` / `--help`) powered by **JCommander**.
- **Graceful Shutdown**: Automatically registers a JVM shutdown hook to close listening sockets and clean up worker threads on termination.
- **Logging**: Configured with **SLF4J** for detailed operational logging and debugging.
- **Comprehensive Unit & Integration Tests**: Built with **JUnit 5** to test single-client and multi-client echo functionality.
- **Fat JAR Packaging**: Customized Gradle build configuration to produce a runnable standalone JAR.

---

## Prerequisites

- **Java Development Kit (JDK)** 11 or higher
- **Gradle** 7+ (or use the included `./gradlew` wrapper)

---

## Building and Running

### 1. Build the Project & Fat JAR
To compile the source code, run tests, and assemble the runnable fat JAR:
```bash
./gradlew build
```

### 2. Run the Server

#### Option A: Using Gradle Wrapper
Run the server with the default port (`1234`):
```bash
./gradlew run
```

Or pass custom CLI arguments:
```bash
./gradlew run --args="-p 8080"
```

Display command-line help options:
```bash
./gradlew run --args="--help"
```

#### Option B: Running the Executable Fat JAR
After building, run the generated executable JAR directly:
```bash
java -jar build/libs/echo-server-1.0-SNAPSHOT.jar -p 8080
```

---

## Testing

### Automated Unit & Integration Tests
Run the test suite using Gradle:
```bash
./gradlew test
```

### Manual Testing with Netcat (`nc`)
Start the server on port `1234`, then open a terminal and connect:
```bash
nc localhost 1234
```
Type any message and press **Enter** — the server will echo your message back immediately.

---

## License

This project is licensed under the [MIT License](LICENSE).
