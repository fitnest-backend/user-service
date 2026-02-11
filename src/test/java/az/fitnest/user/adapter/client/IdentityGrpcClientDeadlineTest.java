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
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
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
    void deadlineExceededOnSlowServer() {
        IdentityGrpcClient client = new IdentityGrpcClient();
        // Manually inject stub via reflection for the test
        io.grpc.ManagedChannel channel = io.grpc.ManagedChannelBuilder.forAddress("localhost", port).usePlaintext().build();
        UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);
        try {
            java.lang.reflect.Field f = IdentityGrpcClient.class.getDeclaredField("userServiceStub");
            f.setAccessible(true);
            f.set(client, stub);
        } catch (Exception e) {
            fail("Failed to set stub: " + e.getMessage());
        }
        assertThrows(io.grpc.StatusRuntimeException.class, () -> client.getUserById(123L));
        channel.shutdownNow();
    }
}
