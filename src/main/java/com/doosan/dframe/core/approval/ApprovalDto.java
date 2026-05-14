package com.example.baseb.common.approval;

import java.time.LocalDateTime;
import java.util.List;

public record ApprovalDto(
        String id,
        String title,
        String description,
        String targetType,
        List<String> targetIds,
        List<String> fileIds,
        String requesterId,
        ApprovalStatus status,
        List<ApprovalLineDto> approvalLines,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ApprovalDto fromEntity(Approval approval) {
        return new ApprovalDto(
                approval.getId(),
                approval.getTitle(),
                approval.getDescription(),
                approval.getTargetType(),
                approval.getTargetIds(),
                approval.getFileIds(),
                approval.getRequesterId(),
                approval.getStatus(),
                approval.getApprovalLines().stream()
                        .map(ApprovalLineDto::fromEntity)
                        .toList(),
                approval.getCreatedAt(),
                approval.getUpdatedAt()
        );
    }
}
