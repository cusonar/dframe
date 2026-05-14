package com.doosan.dframe.core.employee;

import com.doosan.dframe.core.config.audit.BaseEntity;
import com.doosan.dframe.core.department.Department;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity implements UserDetails {

    @Id
    private String id;

    private String password;

    @Column(nullable = false)
    private String name;

    private String englishName;

    private String position;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_code")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_dispatch_code")
    private Department dispatchDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_work_code")
    private Department workDepartment;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<EmployeeRole> employeeRoles = new HashSet<>();

    // Spring Security fields
    @Builder.Default
    private int countLoginFail = 0;

    private LocalDateTime lastLoginAt;

    private LocalDateTime lastPasswordChangedAt;

    @Builder.Default
    private boolean accountNonExpired = true;

    @Builder.Default
    private boolean accountNonLocked = true;

    @Builder.Default
    private boolean credentialsNonExpired = true;

    @Builder.Default
    private boolean enabled = true;

    // Helper to get authorities from roles
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (EmployeeRole er : employeeRoles) {
            // Add the Role itself (e.g. ROLE_ADMIN)
            authorities.add(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority(er.getRole().getCode()));
            // Add the specific operations (e.g. OP_READ)
            authorities.addAll(er.getRole().getAuthorities());
        }
        return authorities;
    }

    // Methods to update state
    public void incrementLoginFail() {
        this.countLoginFail++;
    }

    public void resetLoginFail() {
        this.countLoginFail = 0;
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void update(String name, String englishName, String email, String phone, String position,
                       Department department, Department dispatchDepartment, Department workDepartment) {
        if (name != null)
            this.name = name;
        if (englishName != null)
            this.englishName = englishName;
        if (email != null)
            this.email = email;
        if (phone != null)
            this.phone = phone;
        if (position != null)
            this.position = position;
        if (department != null)
            this.department = department;
        if (dispatchDepartment != null)
            this.dispatchDepartment = dispatchDepartment;
        if (workDepartment != null)
            this.workDepartment = workDepartment;
    }

    public void updateStatus(Boolean enabled) {
        if (enabled != null)
            this.enabled = enabled;
    }

    public void updateAuthStatus(Boolean accountNonExpired, Boolean credentialsNonExpired, Boolean accountNonLocked, Integer countLoginFail) {
        if (accountNonExpired != null) this.accountNonExpired = accountNonExpired;
        if (credentialsNonExpired != null) this.credentialsNonExpired = credentialsNonExpired;
        if (accountNonLocked != null) this.accountNonLocked = accountNonLocked;
        if (countLoginFail != null) this.countLoginFail = countLoginFail;
    }

    public String getUsername() {
        return id;
    }
}
