package com.doosan.dframe.approval;

import com.doosan.dframe.admin.employee.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalLineRepository extends JpaRepository<ApprovalLine, Long> {
    @Query("SELECT al FROM ApprovalLine al JOIN FETCH al.document d WHERE al.approver = :approver AND al.status = :status ORDER BY d.createdAt DESC")
    Page<ApprovalLine> findByApproverAndStatus(@Param("approver") Employee approver, @Param("status") ApprovalLineStatus status, Pageable pageable);
}
