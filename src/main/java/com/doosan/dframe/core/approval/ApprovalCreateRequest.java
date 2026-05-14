package com.doosan.dframe.core.approval;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ApprovalCreateRequest(
        @NotBlank String title,
        String description,
        @NotBlank String targetType,
        List<String> targetIds,
        List<String> fileIds,
        @NotEmpty List<ApproverRequest> approvers
) {
    public record ApproverRequest(
            @NotBlank String id,
            ApprovalType type
    ) {
        public ApproverRequest {
            if (type == null) type = ApprovalType.APPROVE;
        }
    }
}
