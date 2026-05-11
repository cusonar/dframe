package com.doosan.dframe.approval;

import com.doosan.dframe.admin.employee.Employee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class ApprovalLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private ApprovalDocument document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private Employee approver;

    private int approvalOrder;

    @Enumerated(EnumType.STRING)
    private ApprovalLineStatus status = ApprovalLineStatus.WAITING;

    private String comment;

    private LocalDateTime processedAt;
}
