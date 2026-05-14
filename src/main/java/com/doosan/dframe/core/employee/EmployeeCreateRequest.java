package com.doosan.dframe.core.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record EmployeeCreateRequest(
        @NotBlank String id,
        @NotBlank String password,
        @NotBlank String name,
        @NotBlank @Email String email,
        String phone,
        String deptCode,
        String dispatchDeptCode,
        String workDeptCode,
        List<String> roleCodes) {
}
