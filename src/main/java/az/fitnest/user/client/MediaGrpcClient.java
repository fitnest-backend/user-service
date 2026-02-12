package az.fitnest.user.client;

import az.fitnest.media.grpc.DeleteFilesRequest;
import az.fitnest.media.grpc.MediaServiceGrpc;
import az.fitnest.media.grpc.UploadImageRequest;
import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MediaGrpcClient {

    @GrpcClient("media-service")
    private MediaServiceGrpc.MediaServiceBlockingStub mediaServiceStub;

    @Value("${grpc.media.deadline-ms:15000}")
    private long deadlineMs;

    private MediaServiceGrpc.MediaServiceBlockingStub withDeadline() {
        return mediaServiceStub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS);
    }

    public az.fitnest.media.grpc.MediaUploadResponse uploadImage(MultipartFile file) throws IOException {
        long start = System.currentTimeMillis();
        try {
            log.debug("Calling media-service gRPC UploadImage filename={}, size={}, deadlineMs={}", 
                    file.getOriginalFilename(), file.getSize(), deadlineMs);

            UploadImageRequest request = UploadImageRequest.newBuilder()
                    .setImageData(ByteString.copyFrom(file.getBytes()))
                    .setFilename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload")
                    .setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream")
                    .build();

            az.fitnest.media.grpc.MediaUploadResponse response = withDeadline().uploadImage(request);
            log.debug("gRPC uploadImage success in {}ms", System.currentTimeMillis() - start);
            return response;
        } catch (StatusRuntimeException e) {
            log.error("gRPC uploadImage failed - Status: {}, Code: {}, Message: {}, durationMs={}",
                    e.getStatus().getCode(), e.getStatus().getCode(), e.getStatus().getDescription(), 
                    System.currentTimeMillis() - start, e);
            throw e;
        }
    }

    public az.fitnest.media.grpc.MediaDeleteResponse deleteFiles(List<String> filePaths) {
        long start = System.currentTimeMillis();
        try {
            log.debug("Calling media-service gRPC DeleteFiles count={}, deadlineMs={}", filePaths.size(), deadlineMs);

            DeleteFilesRequest request = DeleteFilesRequest.newBuilder()
                    .addAllFilePaths(filePaths)
                    .build();

            az.fitnest.media.grpc.MediaDeleteResponse response = withDeadline().deleteFiles(request);
            log.debug("gRPC deleteFiles success in {}ms", System.currentTimeMillis() - start);
            return response;
        } catch (StatusRuntimeException e) {
            log.error("gRPC deleteFiles failed - Status: {}, Code: {}, Message: {}, durationMs={}",
                    e.getStatus().getCode(), e.getStatus().getCode(), e.getStatus().getDescription(), 
                    System.currentTimeMillis() - start, e);
            throw e;
        }
    }
}
