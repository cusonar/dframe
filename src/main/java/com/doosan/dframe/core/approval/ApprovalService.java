package com.doosan.dframe.core.approval;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final ApprovalLineRepository approvalLineRepository;

    @Transactional
    public ApprovalDto createApproval(String requesterId, ApprovalCreateRequest request) {
        Approval approval = Approval.builder()
                .title(request.title())
                .description(request.description())
                .targetType(request.targetType())
                .targetIds(request.targetIds() != null ? request.targetIds() : new java.util.ArrayList<>())
                .fileIds(request.fileIds() != null ? request.fileIds() : new java.util.ArrayList<>())
                .requesterId(requesterId)
                .status(ApprovalStatus.PENDING)
                .build();

        Approval savedApproval = approvalRepository.save(approval);

        for (int i = 0; i < request.approvers().size(); i++) {
            var approver = request.approvers().get(i);
            ApprovalLine line = ApprovalLine.builder()
                    .approval(savedApproval)
                    .seq(i + 1)
                    .type(approver.type())
                    .approverId(approver.id())
                    .status(ApprovalStatus.PENDING)
                    .build();
            savedApproval.getApprovalLines().add(line);
        }

        return ApprovalDto.fromEntity(savedApproval);
    }

    @Transactional(readOnly = true)
    public List<ApprovalDto> getMyRequests(String requesterId) {
        return approvalRepository.findByRequesterIdOrderByCreatedAtDesc(requesterId).stream()
                .map(ApprovalDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApprovalDto> getMyApprovals(String approverId) {
        List<ApprovalLine> lines = approvalLineRepository.findByApproverIdOrderByCreatedAtDesc(approverId);
        return lines.stream()
                .map(line -> ApprovalDto.fromEntity(line.getApproval()))
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    public ApprovalDto getApprovalById(String id) {
        Approval approval = approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval not found"));
        return ApprovalDto.fromEntity(approval);
    }

    @Transactional
    public ApprovalDto approve(String approvalId, String lineId, String approverId, ApprovalActionRequest request) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        ApprovalLine line = approval.getApprovalLines().stream()
                .filter(l -> l.getId().equals(lineId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Approval line not found"));

        if (!line.getApproverId().equals(approverId)) {
            throw new RuntimeException("You are not the approver for this line");
        }

        if (line.getStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("This approval line has already been processed");
        }

        // Check if previous lines are all approved (sequential approval)
        for (ApprovalLine prevLine : approval.getApprovalLines()) {
            if (prevLine.getSeq() < line.getSeq() && prevLine.getStatus() != ApprovalStatus.APPROVED) {
                throw new RuntimeException("Previous approval lines must be approved first");
            }
        }

        line.approve(request.comment());

        // Check if all lines are approved
        boolean allApproved = approval.getApprovalLines().stream()
                .allMatch(l -> l.getStatus() == ApprovalStatus.APPROVED);
        if (allApproved) {
            approval.updateStatus(ApprovalStatus.APPROVED);
        }

        return ApprovalDto.fromEntity(approval);
    }

    @Transactional
    public ApprovalDto reject(String approvalId, String lineId, String approverId, ApprovalActionRequest request) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        ApprovalLine line = approval.getApprovalLines().stream()
                .filter(l -> l.getId().equals(lineId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Approval line not found"));

        if (!line.getApproverId().equals(approverId)) {
            throw new RuntimeException("You are not the approver for this line");
        }

        if (line.getStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("This approval line has already been processed");
        }

        line.reject(request.comment());
        approval.updateStatus(ApprovalStatus.REJECTED);

        return ApprovalDto.fromEntity(approval);
    }
}
