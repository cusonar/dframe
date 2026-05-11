package com.doosan.dframe.domain;

public enum ApprovalStatus {
    DRAFT,          // 임시저장
    IN_PROGRESS,    // 결재 진행 중
    APPROVED,       // 승인 완료
    REJECTED        // 반려됨
}
