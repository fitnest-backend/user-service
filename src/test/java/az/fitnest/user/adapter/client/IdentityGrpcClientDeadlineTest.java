package az.fitnest.user.adapter.client;

import az.fitnest.user.client.IdentityGrpcClient;
import az.fitnest.user.grpc.GetUserByIdRequest;
import az.fitnest.user.grpc.UserResponse;
import az.fitnest.user.grpc.UserServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IdentityGrpcClientDeadlineTest {
    static Server server;
    static int port = 50051;

    @BeforeAll
    static void startServer() throws Exception {
        server = ServerBuilder.forPort(port).addService(new UserServiceGrpc.UserServiceImplBase() {
            @Override
            public void getUserById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
                // Simulate slow response beyond client deadline
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
                responseObserver.onNext(UserResponse.newBuilder().setUserId(request.getUserId()).build());
                responseObserver.onCompleted();
            }
        }).build();
        server.start();
    }

    @AfterAll
    static void stopServer() throws Exception {
        server.shutdownNow();
    }

    @Test
    void respectsIncreasedDeadline() throws Exception {
        IdentityGrpcClient client = new IdentityGrpcClient();

        // Inject 10s deadline
        java.lang.reflect.Field deadlineField = IdentityGrpcClient.class.getDeclaredField("deadlineMs");
        deadlineField.setAccessible(true);
        deadlineField.set(client, 10000L);

        io.grpc.ManagedChannel channel = io.grpc.ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build();
        UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);

        java.lang.reflect.Field stubField = IdentityGrpcClient.class.getDeclaredField("userServiceStub");
        stubField.setAccessible(true);
        stubField.set(client, stub);

        // Slow server (3s) should now pass with 10s deadline
        assertDoesNotThrow(() -> client.getUserById(123L));

        channel.shutdownNow();
    }
}
