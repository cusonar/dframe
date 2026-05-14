package com.doosan.dframe.core.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileInfoService {

    private final FileInfoRepository fileInfoRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Transactional
    public FileInfoResponse uploadFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String originalFilename = StringUtils
                    .cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown");
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex > 0) {
                extension = originalFilename.substring(dotIndex);
            }
            String savedFilename = UUID.randomUUID() + extension;
            Path targetLocation = uploadPath.resolve(savedFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            FileInfo fileInfo = FileInfo.builder()
                    .originalFilename(originalFilename)
                    .savedFilename(savedFilename)
                    .filePath(targetLocation.toString())
                    .contentType(file.getContentType())
                    .fileSize(file.getSize())
                    .build();

            fileInfoRepository.save(fileInfo);

            String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/v1/files/")
                    .path(fileInfo.getId())
                    .path("/download")
                    .toUriString();

            return FileInfoResponse.from(fileInfo, downloadUrl);
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file", ex);
        }
    }

    @Transactional(readOnly = true)
    public FileInfo getFileInfo(String id) {
        return fileInfoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("File not found with id " + id));
    }

    public Resource loadFileAsResource(String id) {
        try {
            FileInfo fileInfo = getFileInfo(id);
            Path filePath = Paths.get(fileInfo.getFilePath()).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found or not readable: " + fileInfo.getOriginalFilename());
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found", ex);
        }
    }
}
