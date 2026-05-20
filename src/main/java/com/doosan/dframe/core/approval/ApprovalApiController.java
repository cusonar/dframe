package com.doosan.dframe.core.approval;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalApiController {

    private final ApprovalService approvalService;

    @PostMapping
    public ResponseEntity<ApprovalDto> createApproval(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ApprovalCreateRequest request) {
        return ResponseEntity.ok(approvalService.createApproval(userDetails.getUsername(), request));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<ApprovalDto>> getMyRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(approvalService.getMyRequests(userDetails.getUsername()));
    }

    @GetMapping("/my-approvals")
    public ResponseEntity<List<ApprovalDto>> getMyApprovals(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(approvalService.getMyApprovals(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalDto> getApprovalById(@PathVariable String id) {
        return ResponseEntity.ok(approvalService.getApprovalById(id));
    }

    @PostMapping("/{id}/lines/{lineId}/approve")
    public ResponseEntity<ApprovalDto> approve(
            @PathVariable String id,
            @PathVariable String lineId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody(required = false) ApprovalActionRequest request) {
        if (request == null) {
            request = new ApprovalActionRequest(null);
        }
        return ResponseEntity.ok(approvalService.approve(id, lineId, userDetails.getUsername(), request));
    }

    @PostMapping("/{id}/lines/{lineId}/reject")
    public ResponseEntity<ApprovalDto> reject(
            @PathVariable String id,
            @PathVariable String lineId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody(required = false) ApprovalActionRequest request) {
        if (request == null) {
            request = new ApprovalActionRequest(null);
        }
        return ResponseEntity.ok(approvalService.reject(id, lineId, userDetails.getUsername(), request));
    }
}
