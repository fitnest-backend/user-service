package az.fitnest.user.client;

import az.fitnest.storage.grpc.DeleteFilesRequest;
import az.fitnest.storage.grpc.DeleteFilesResponse;
import az.fitnest.storage.grpc.FileMetadata;
import az.fitnest.storage.grpc.GetDownloadUrlRequest;
import az.fitnest.storage.grpc.GetDownloadUrlResponse;
import az.fitnest.storage.grpc.StorageServiceGrpc;
import az.fitnest.storage.grpc.UploadFileRequest;
import az.fitnest.storage.grpc.UploadFileResponse;
import az.fitnest.user.dto.response.StorageFileData;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class StorageGrpcClient {

    @GrpcClient("storage-service")
    private StorageServiceGrpc.StorageServiceStub asyncStub;

    @GrpcClient("storage-service")
    private StorageServiceGrpc.StorageServiceBlockingStub blockingStub;

    public StorageFileData uploadFile(MultipartFile file, String directory) {
        return uploadFile(file, directory, null);
    }

    public StorageFileData uploadFile(MultipartFile file, String directory, String oldPath) {
        final CountDownLatch finishLatch = new CountDownLatch(1);
        final AtomicReference<StorageFileData> responseData = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();

        StreamObserver<UploadFileResponse> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(UploadFileResponse response) {
                if (response.getSuccess()) {
                    az.fitnest.storage.grpc.StorageFileData grpcData = response.getData();
                    StorageFileData data = new StorageFileData();
                    data.setPath(grpcData.getPath());
                    data.setSize(grpcData.getSize());
                    data.setMd5(grpcData.getMd5());
                    data.setFsId(grpcData.getFsId());
                    responseData.set(data);
                }
            }

            @Override
            public void onError(Throwable t) {
                error.set(t);
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        };

        StreamObserver<UploadFileRequest> requestObserver = asyncStub.uploadFile(responseObserver);

        try {
            // Send metadata
            FileMetadata.Builder metadataBuilder = FileMetadata.newBuilder()
                    .setFilename(file.getOriginalFilename())
                    .setDirectory(directory != null ? directory : "/uploads")
                    .setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");

            if (oldPath != null) {
                metadataBuilder.setOldPath(oldPath);
            }

            requestObserver.onNext(UploadFileRequest.newBuilder().setMetadata(metadataBuilder.build()).build());

            // Send file content in chunks
            byte[] buffer = new byte[1024 * 64]; // 64KB chunks
            try (InputStream inputStream = file.getInputStream()) {
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    requestObserver.onNext(UploadFileRequest.newBuilder()
                            .setChunkData(ByteString.copyFrom(buffer, 0, bytesRead))
                            .build());
                }
            }

            requestObserver.onCompleted();

            // Wait for completion (max 5 minutes for upload)
            if (!finishLatch.await(5, TimeUnit.MINUTES)) {
                throw new RuntimeException("Upload timed out");
            }

            if (error.get() != null) {
                throw new RuntimeException("Upload failed: " + error.get().getMessage(), error.get());
            }

            return responseData.get();

        } catch (IOException | InterruptedException e) {
            requestObserver.onError(e);
            throw new RuntimeException("Upload interrupted or failed", e);
        }
    }

    public String getDownloadUrl(String fileId) {
        GetDownloadUrlRequest request = GetDownloadUrlRequest.newBuilder()
                .setFileId(fileId)
                .build();
        GetDownloadUrlResponse response = blockingStub.getDownloadUrl(request);
        if (response.getSuccess()) {
            return response.getDownloadUrl();
        } else {
            throw new RuntimeException("Download failed: " + response.getMessage());
        }
    }

    public void deleteFiles(List<String> paths) {
        DeleteFilesRequest request = DeleteFilesRequest.newBuilder()
                .addAllPaths(paths)
                .build();
        DeleteFilesResponse response = blockingStub.deleteFiles(request);
        if (!response.getSuccess()) {
            throw new RuntimeException("Delete failed: " + response.getMessage());
        }
    }

    public void downloadFile(String fileId, java.util.function.Consumer<az.fitnest.storage.grpc.DownloadFileResponse> observer) {
        az.fitnest.storage.grpc.DownloadFileRequest request = az.fitnest.storage.grpc.DownloadFileRequest.newBuilder()
                .setFileId(fileId)
                .build();

        blockingStub.downloadFile(request).forEachRemaining(observer);
    }
}
