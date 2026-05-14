package com.example.baseb.common.file;

import com.example.baseb.common.config.audit.BaseEntityWithId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "file_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FileInfo extends BaseEntityWithId {

    private String originalFilename;
    private String savedFilename;
    private String filePath;
    private String contentType;
    private Long fileSize;

    @Builder
    public FileInfo(String originalFilename, String savedFilename, String filePath, String contentType, Long fileSize) {
        this.originalFilename = originalFilename;
        this.savedFilename = savedFilename;
        this.filePath = filePath;
        this.contentType = contentType;
        this.fileSize = fileSize;
    }
}
