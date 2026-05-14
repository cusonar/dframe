package com.example.baseb.common.file;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileInfoService fileInfoService;

    @PostMapping
    public ResponseEntity<FileInfoResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        FileInfoResponse response = fileInfoService.uploadFile(file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileInfoResponse> getFileInfo(@PathVariable("id") String id) {
        FileInfo fileInfo = fileInfoService.getFileInfo(id);

        // This creates a download URL similarly to the service, though the service
        // returns it on upload too.
        String downloadUrl = org.springframework.web.servlet.support.ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/v1/files/")
                .path(fileInfo.getId())
                .path("/download")
                .toUriString();

        return ResponseEntity.ok(FileInfoResponse.from(fileInfo, downloadUrl));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") String id) {
        Resource resource = fileInfoService.loadFileAsResource(id);
        FileInfo fileInfo = fileInfoService.getFileInfo(id);

        String contentType = fileInfo.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileInfo.getOriginalFilename() + "\"")
                .body(resource);
    }
}
