package com.example.baseb.common.role;

import com.example.baseb.common.config.audit.BaseEntity;
import com.example.baseb.common.employee.EmployeeRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Id
    private String code;

    private String description;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<EmployeeRole> employeeRoles = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RoleAuthority> roleAuthorities = new HashSet<>();

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleAuthorities.stream()
                .map(ra -> new SimpleGrantedAuthority(ra.getAuthority().getCode()))
                .collect(Collectors.toSet());
    }
}
