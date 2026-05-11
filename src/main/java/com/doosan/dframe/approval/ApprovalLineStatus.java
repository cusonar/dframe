package com.doosan.dframe.approval;

public enum ApprovalLineStatus {
    WAITING, // 대기 중 (이전 결재자가 아직 결재 안 함)
    PENDING, // 결재 대기 중 (현재 내 차례)
    APPROVED, // 승인
    REJECTED // 반려
}
