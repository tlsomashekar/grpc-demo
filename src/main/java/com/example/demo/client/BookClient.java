package com.example.demo.client;

import com.java.employee.BookRequest;
import com.java.employee.BookResponse;
import com.java.employee.BookServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Tiny blocking gRPC client for the BookService.
 * Usage: java -cp target/demo-0.0.1-SNAPSHOT.jar com.example.demo.client.BookClient [host] [port] [bookId]
 * Defaults: host=localhost, port=9090, bookId=123
 */
public class BookClient {

    public static void main(String[] args) throws InterruptedException {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 9090;
        String bookId = args.length > 2 ? args[2] : "123";

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        try {
            BookServiceGrpc.BookServiceBlockingStub stub = BookServiceGrpc.newBlockingStub(channel);

            BookRequest req = BookRequest.newBuilder()
                    .setBookId(bookId)
                    .build();

            BookResponse resp = stub.getBook(req);

            System.out.println("Response received:");
            System.out.println("book_id: " + resp.getBookId());
            System.out.println("name: " + resp.getName());
            System.out.println("type: " + resp.getType());

        } catch (Exception e) {
            System.err.println("RPC failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}

