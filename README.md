# grpc-demo

This is a small Spring Boot gRPC demo that exposes a `BookService` (defined in `src/main/proto/book-service.proto`).

This README explains how to build and run the server, how to test it with grpcurl on Windows, and a short overview of how gRPC works.

---

## Quick overview

- Service: `BookService`
- RPC: `getBook(BookRequest) returns (BookResponse)`
- Proto files: `src/main/proto/book-service.proto`, `src/main/proto/constants/util.proto`
- The proto file does not declare a `package` statement, so the service name visible to gRPC clients is `BookService`.
- The project includes `grpc-services` (reflection) and `spring-grpc-spring-boot-starter` in `pom.xml`.

## How gRPC works (short)

- gRPC is an RPC framework built on HTTP/2 and uses protocol buffers (protobuf) as the IDL and message format.
- You define services and messages in `.proto` files. From those files you generate server and client code (this project does that during the Maven build).
- A client calls a remote method as if it were a local method. gRPC supports unary RPCs and streaming (client, server, bidi).
- Server reflection (optional) allows tools like `grpcurl` to query the server for available services and methods at runtime.

## Build

Open a Windows command prompt in the project root (`c:\Users\somas\workspace\grpc-demo`) and run:

```bat
mvn -e -DskipTests package
```

This will run the protobuf generation plugin and build the application JAR.

## Run

Option A — run from Maven (dev):

```bat
mvn spring-boot:run
```

Option B — run the packaged JAR:

```bat
java -jar target\demo-0.0.1-SNAPSHOT.jar
```

By default the gRPC server used by the `spring-grpc` starter listens on port `9090` (unless you configured a different port). If you changed the configuration, use that port in the examples below.

If you want to explicitly set the port, add to `src/main/resources/application.properties`:

```ini
# example - change the gRPC server port
grpc.server.port=50051
```

## Test the gRPC server with grpcurl (Windows)

1) Install grpcurl

- Using Chocolatey (recommended on Windows):

```bat
choco install grpcurl
```

- Or download the `grpcurl.exe` release from the grpcurl GitHub releases page and put it on your PATH.

2) Use grpcurl to inspect services (server must be running)

List exposed services (uses reflection if available):

```bat
grpcurl -plaintext localhost:9090 list
```

Describe the service and its methods:

```bat
grpcurl -plaintext localhost:9090 describe BookService
```

3) Call the `getBook` method

A sample request JSON for the unary `getBook` RPC (field name is `book_id`):

```json
{"book_id":"123"}
```

Invoke the RPC. Note the JSON quoting for Windows `cmd.exe` (escape inner double quotes):

```bat
grpcurl -plaintext -d "{\"book_id\":\"123\"}" localhost:9090 BookService/getBook
```

If reflection is disabled or not available, point `grpcurl` to the proto directly:

```bat
grpcurl -plaintext -import-path src\main\proto -proto src\main\proto\book-service.proto -d "{\"book_id\":\"123\"}" localhost:9090 BookService/getBook
```

You should get a JSON response similar to:

```json
{
  "book_id": "123",
  "name": "java",
  "type": "AUTOBIOGRAPHY"
}
```

Notes:
- The proto in this project did not declare a `package`, so the service is known as `BookService` at the wire-level.
- The generated Java classes live under the `com.java.employee` package because `option java_package` was set in the proto; this does not change the gRPC wire service name.

## Troubleshooting

- "'grpcurl' is not recognized as an internal or external command" — grpcurl is not on your PATH. Either install via Chocolatey (`choco install grpcurl`) or download the `grpcurl.exe` and add it to PATH.
- If you see an UNAVAILABLE/connection error, ensure the server is running and the port is correct. If using TLS, remove `-plaintext` and configure TLS options accordingly.
- If grpcurl can't list services, server-side reflection may not be enabled; use the `-import-path` and `-proto` flags to provide the proto file directly.

## Useful references

- This project's proto files: `src/main/proto/*.proto`
- Build: `mvn package` (protobuf generation is performed by the Maven plugin)
- Run: `mvn spring-boot:run` or `java -jar target\demo-0.0.1-SNAPSHOT.jar`

---

If you want, I can also:
- add a small Java-based integration test that uses the `spring-grpc-test` dependency to call `getBook` directly;
- or create a tiny Java client class with a main() method that calls the gRPC endpoint so you can run it without grpcurl.

Tell me which you'd prefer and I'll add it.

