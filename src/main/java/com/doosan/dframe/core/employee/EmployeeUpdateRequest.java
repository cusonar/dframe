package com.doosan.dframe.core.employee;

import jakarta.validation.constraints.Email;

import java.util.List;

public record EmployeeUpdateRequest(
        String name,
        String englishName,
        @Email String email,
        String phone,
        String position,
        String deptCode,
        String dispatchDeptCode,
        String workDeptCode,
        List<String> roleCodes,
        Boolean enabled,
        Boolean accountNonExpired,
        Boolean credentialsNonExpired,
        Boolean accountNonLocked,
        Integer countLoginFail) {
}
