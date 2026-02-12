package az.fitnest.user.service;

import az.fitnest.user.exception.BadRequestException;
import az.fitnest.user.exception.BaseException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String saveFile(MultipartFile file);
    void deleteFile(String fileUrl);
    void deleteFiles(List<String> fileUrls);
}
