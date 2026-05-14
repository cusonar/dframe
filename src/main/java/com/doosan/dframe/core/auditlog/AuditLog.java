package com.doosan.dframe.core.auditlog;

import com.doosan.dframe.core.config.audit.BaseEntityWithId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "audit")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntityWithId {

    @Column(nullable = false)
    private String action;

    @Column(length = 1000)
    private String detail;

    private String ipAddress;

    @Column(nullable = false)
    private String employeeId;

}
