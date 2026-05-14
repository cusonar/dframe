package com.example.baseb.common.approval;

import java.time.LocalDateTime;

public record ApprovalLineDto(
        String id,
        int seq,
        ApprovalType type,
        String approverId,
        ApprovalStatus status,
        String comment,
        LocalDateTime processedAt,
        LocalDateTime createdAt
) {
    public static ApprovalLineDto fromEntity(ApprovalLine line) {
        return new ApprovalLineDto(
                line.getId(),
                line.getSeq(),
                line.getType(),
                line.getApproverId(),
                line.getStatus(),
                line.getComment(),
                line.getProcessedAt(),
                line.getCreatedAt()
        );
    }
}
