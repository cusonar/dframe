package com.doosan.dframe.approval;

import com.doosan.dframe.admin.employee.Employee;
import com.doosan.dframe.admin.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ApprovalService {
    private final ApprovalDocumentRepository documentRepository;
    private final ApprovalLineRepository lineRepository;
    private final EmployeeRepository employeeRepository;

    public ApprovalDocument draftDocument(Long drafterId, String title, String content, List<Long> approverIds) {
        Employee drafter = employeeRepository.findById(drafterId).orElseThrow();
        
        ApprovalDocument doc = new ApprovalDocument();
        doc.setTitle(title);
        doc.setContent(content);
        doc.setDrafter(drafter);
        doc.setStatus(ApprovalStatus.IN_PROGRESS);
        
        int order = 1;
        for (Long approverId : approverIds) {
            Employee approver = employeeRepository.findById(approverId).orElseThrow();
            ApprovalLine line = new ApprovalLine();
            line.setDocument(doc);
            line.setApprover(approver);
            line.setApprovalOrder(order);
            line.setStatus(order == 1 ? ApprovalLineStatus.PENDING : ApprovalLineStatus.WAITING);
            doc.getApprovalLines().add(line);
            order++;
        }
        
        return documentRepository.save(doc);
    }

    public void processApproval(Long lineId, boolean isApproved, String comment) {
        ApprovalLine line = lineRepository.findById(lineId).orElseThrow();
        if (line.getStatus() != ApprovalLineStatus.PENDING) {
            throw new IllegalStateException("Not in pending state");
        }

        line.setStatus(isApproved ? ApprovalLineStatus.APPROVED : ApprovalLineStatus.REJECTED);
        line.setComment(comment);
        line.setProcessedAt(LocalDateTime.now());

        ApprovalDocument doc = line.getDocument();

        if (!isApproved) {
            doc.setStatus(ApprovalStatus.REJECTED);
        } else {
            // Find next approver
            ApprovalLine nextLine = doc.getApprovalLines().stream()
                .filter(l -> l.getApprovalOrder() == line.getApprovalOrder() + 1)
                .findFirst().orElse(null);

            if (nextLine != null) {
                nextLine.setStatus(ApprovalLineStatus.PENDING);
            } else {
                doc.setStatus(ApprovalStatus.APPROVED);
            }
        }
        
        doc.setUpdatedAt(LocalDateTime.now());
    }
}
