package com.example.baseb.common.approval;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, String> {

    List<ApprovalLine> findByApproverIdAndStatusOrderByCreatedAtDesc(String approverId, ApprovalStatus status);

    List<ApprovalLine> findByApproverIdOrderByCreatedAtDesc(String approverId);
}
