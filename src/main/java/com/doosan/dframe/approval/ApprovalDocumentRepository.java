package com.doosan.dframe.repository;

import com.doosan.dframe.domain.ApprovalDocument;
import com.doosan.dframe.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalDocumentRepository extends JpaRepository<ApprovalDocument, Long> {
    Page<ApprovalDocument> findByDrafter(Employee drafter, Pageable pageable);
}
