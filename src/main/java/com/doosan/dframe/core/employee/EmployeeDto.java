package com.doosan.dframe.core.employee;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record EmployeeDto(
        String id,
        String name,
        String email,
        String englishName,
        String position,
        String phone,
        String deptCode,
        String deptName,
        String dispatchDeptCode,
        String dispatchDeptName,
        String workDeptCode,
        String workDeptName,
        boolean enabled,
        boolean accountNonExpired,
        boolean accountNonLocked,
        boolean credentialsNonExpired,
        int countLoginFail,
        LocalDateTime lastPasswordChangedAt,
        String roleCodes,
        LocalDateTime lastLoginAt) {

    public static EmployeeDto fromEntity(Employee e) {
        // roleCodes: List -> 쉼표 구분 String
        List<String> roleList = e.getEmployeeRoles().stream()
                .map(er -> er.getRole().getCode())
                .toList();
        String roleCodesStr = String.join(",", roleList);

        return EmployeeDto.builder()
                .id(e.getId())
                .name(e.getName())
                .email(e.getEmail())
                .englishName(e.getEnglishName())
                .position(e.getPosition())
                .phone(e.getPhone())
                .deptCode(e.getDepartment() != null ? e.getDepartment().getCode() : null)
                .deptName(e.getDepartment() != null ? e.getDepartment().getName() : null)
                .dispatchDeptCode(e.getDispatchDepartment() != null ? e.getDispatchDepartment().getCode() : null)
                .dispatchDeptName(e.getDispatchDepartment() != null ? e.getDispatchDepartment().getName() : null)
                .workDeptCode(e.getWorkDepartment() != null ? e.getWorkDepartment().getCode() : null)
                .workDeptName(e.getWorkDepartment() != null ? e.getWorkDepartment().getName() : null)
                .enabled(e.isEnabled())
                .accountNonExpired(e.isAccountNonExpired())
                .accountNonLocked(e.isAccountNonLocked())
                .credentialsNonExpired(e.isCredentialsNonExpired())
                .countLoginFail(e.getCountLoginFail())
                .lastPasswordChangedAt(e.getLastPasswordChangedAt())
                .roleCodes(roleCodesStr)
                .lastLoginAt(e.getLastLoginAt())
                .build();
    }
}
