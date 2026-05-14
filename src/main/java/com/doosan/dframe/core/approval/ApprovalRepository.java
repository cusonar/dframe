package com.doosan.dframe.core.approval;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRepository extends JpaRepository<Approval, String> {

    List<Approval> findByRequesterIdOrderByCreatedAtDesc(String requesterId);

    List<Approval> findByStatusOrderByCreatedAtDesc(ApprovalStatus status);

    List<Approval> findAllByOrderByCreatedAtDesc();
}
