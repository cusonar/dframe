package com.example.baseb.common.audit;

import com.example.baseb.common.config.audit.BaseEntityWithId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
