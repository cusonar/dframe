package com.example.baseb.common.file;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileInfoResponse {
    private String id;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
    private String downloadUrl;

    public static FileInfoResponse from(FileInfo fileInfo, String downloadUrl) {
        return FileInfoResponse.builder()
                .id(fileInfo.getId())
                .originalFilename(fileInfo.getOriginalFilename())
                .contentType(fileInfo.getContentType())
                .fileSize(fileInfo.getFileSize())
                .downloadUrl(downloadUrl)
                .build();
    }
}
