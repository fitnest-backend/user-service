package az.fitnest.user.client;

import az.fitnest.worker.grpc.DeleteFilesRequest;
import az.fitnest.worker.grpc.TeraBoxWorkerGrpc;
import az.fitnest.worker.grpc.UploadFileRequest;
import com.google.protobuf.ByteString;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class TeraBoxGrpcClient {

    @GrpcClient("terabox-worker")
    private TeraBoxWorkerGrpc.TeraBoxWorkerBlockingStub teraBoxWorkerStub;

    @Value("${grpc.terabox.deadline-ms:30000}")
    private long deadlineMs;

    private TeraBoxWorkerGrpc.TeraBoxWorkerBlockingStub withDeadline() {
        return teraBoxWorkerStub.withDeadlineAfter(deadlineMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public az.fitnest.worker.grpc.UploadFileResponse uploadFile(MultipartFile file, String directory) throws IOException {
        long start = System.currentTimeMillis();
        try {
            log.debug("Calling terabox-worker gRPC UploadFile filename={}, size={}, directory={}, deadlineMs={}",
                    file.getOriginalFilename(), file.getSize(), directory, deadlineMs);

            UploadFileRequest request = UploadFileRequest.newBuilder()
                    .setFileData(ByteString.copyFrom(file.getBytes()))
                    .setFilename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload")
                    .setDirectory(directory != null ? directory : "/uploads")
                    .build();

            az.fitnest.worker.grpc.UploadFileResponse response = withDeadline().uploadFile(request);
            log.debug("gRPC uploadFile success in {}ms", System.currentTimeMillis() - start);
            return response;
        } catch (StatusRuntimeException e) {
            log.error("gRPC uploadFile failed - Status: {}, Code: {}, Message: {}, durationMs={}",
                    e.getStatus().getCode(), e.getStatus().getCode(), e.getStatus().getDescription(),
                    System.currentTimeMillis() - start, e);
            throw e;
        }
    }

    public az.fitnest.worker.grpc.DeleteFilesResponse deleteFiles(List<String> paths) {
        long start = System.currentTimeMillis();
        try {
            log.debug("Calling terabox-worker gRPC DeleteFiles paths={}, deadlineMs={}", paths, deadlineMs);

            DeleteFilesRequest request = DeleteFilesRequest.newBuilder().addAllPaths(paths).build();

            az.fitnest.worker.grpc.DeleteFilesResponse response = withDeadline().deleteFiles(request);
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
