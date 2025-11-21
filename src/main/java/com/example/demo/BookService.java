package com.example.demo;

import com.java.constants.Type;
import com.java.employee.BookRequest;
import com.java.employee.BookResponse;
import com.java.employee.BookServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class BookService extends BookServiceGrpc.BookServiceImplBase {


    /**
     * Unary operation to get the book based on book id
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void getBook(BookRequest request, StreamObserver<BookResponse> responseObserver) {

        // We have mocked the employee data.
        // In real world this should be fetched from database or from some other source
        BookResponse empResp = BookResponse.newBuilder().setBookId(request.getBookId()).setName("java")
                .setType(Type.AUTOBIOGRAPHY).build();

        // set the response object
        responseObserver.onNext(empResp);

        // mark process is completed
        responseObserver.onCompleted();
    }
}
