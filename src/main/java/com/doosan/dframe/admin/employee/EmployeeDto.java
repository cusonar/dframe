package com.doosan.dframe.admin.employee;

import lombok.Data;

import java.util.stream.Collectors;

import com.doosan.dframe.admin.role.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeDto {
    private Long id;
    private String name;
    private String jobTitle;
    private String englishName;
    private String roleName;
    private String departmentName;
    private String leaderName;

    public static EmployeeDto of(Employee employee) {
        return builder()
                .id(employee.getId())
                .name(employee.getName())
                .jobTitle(employee.getJobTitle())
                .englishName(employee.getEnglishName())
                .roleName(employee.getRoles() != null
                        ? employee.getRoles().stream().map(Role::getName).collect(Collectors.joining(","))
                        : "")
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : "")
                .leaderName(
                        employee.getDepartment().getLeader() != null ? employee.getDepartment().getLeader().getName()
                                : "")
                .build();
    }
}
