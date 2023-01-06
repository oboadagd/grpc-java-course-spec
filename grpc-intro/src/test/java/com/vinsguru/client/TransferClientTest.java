
package com.vinsguru.client;

import com.vinsguru.models.TransferRequest;
import com.vinsguru.models.TransferServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.util.concurrent.CountDownLatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferClientTest {

    private TransferServiceGrpc.TransferServiceStub asyncStub;

    @BeforeAll
    public void setup(){
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 6565)
                .usePlaintext()
                .build();
        this.asyncStub = TransferServiceGrpc.newStub(managedChannel);
    }

    @Test
    public void transferTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        TransferStreamingResponse response = new TransferStreamingResponse(latch);
        StreamObserver<TransferRequest> requestStreamObserver = this.asyncStub.transfer(response);


        TransferRequest request = TransferRequest.newBuilder()
                .setFromAccount(5)
                .setToAccount(10)
                .setAmount(10)
                .build();
        requestStreamObserver.onNext(request);

        request = TransferRequest.newBuilder()
                .setFromAccount(4)
                .setToAccount(8)
                .setAmount(30)
                .build();
        requestStreamObserver.onNext(request);

        request = TransferRequest.newBuilder()
                .setFromAccount(4)
                .setToAccount(4)
                .setAmount(20)
                .build();
        requestStreamObserver.onNext(request);

        requestStreamObserver.onCompleted();
        latch.await();
    }
}



