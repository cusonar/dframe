package com.doosan.dframe.admin.employee;

import com.doosan.dframe.admin.department.Department;
import com.doosan.dframe.admin.role.Role;
import com.doosan.dframe.admin.department.DepartmentRepository;
import com.doosan.dframe.admin.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public void createEmployee(String username, String name, String password, Long departmentId, List<Long> roleIds, boolean enabled) {
        Employee employee = new Employee();
        employee.setUsername(username);
        employee.setName(name);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setEnabled(enabled);

        if (departmentId != null) {
            Department dept = departmentRepository.findById(departmentId).orElse(null);
            employee.setDepartment(dept);
        }

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleIds.stream()
                .map(id -> roleRepository.findById(id).orElse(null))
                .filter(r -> r != null)
                .collect(Collectors.toSet());
            employee.setRoles(roles);
        }

        employeeRepository.save(employee);
    }

    public void updateEmployee(Long id, String name, String password, Long departmentId, List<Long> roleIds, boolean enabled) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee Id:" + id));
        
        employee.setName(name);
        if (password != null && !password.trim().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(password));
        }
        employee.setEnabled(enabled);

        if (departmentId != null) {
            Department dept = departmentRepository.findById(departmentId).orElse(null);
            employee.setDepartment(dept);
        } else {
            employee.setDepartment(null);
        }

        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleIds.stream()
                .map(roleId -> roleRepository.findById(roleId).orElse(null))
                .filter(r -> r != null)
                .collect(Collectors.toSet());
            employee.setRoles(roles);
        } else {
            employee.getRoles().clear();
        }
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
