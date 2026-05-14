package com.doosan.dframe.core.approval;

import com.doosan.dframe.core.config.audit.BaseEntityWithId;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Approval extends BaseEntityWithId {

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String targetType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "approval_target", joinColumns = @JoinColumn(name = "approval_id"))
    @Column(name = "target_id")
    @Builder.Default
    private List<String> targetIds = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "approval_files", joinColumns = @JoinColumn(name = "approval_id"))
    @Column(name = "file_id")
    @Builder.Default
    private List<String> fileIds = new ArrayList<>();

    @Column(nullable = false)
    private String requesterId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApprovalStatus status = ApprovalStatus.PENDING;

    @OneToMany(mappedBy = "approval", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("seq ASC")
    @Builder.Default
    private List<ApprovalLine> approvalLines = new ArrayList<>();

    public void updateStatus(ApprovalStatus status) {
        this.status = status;
    }
}
