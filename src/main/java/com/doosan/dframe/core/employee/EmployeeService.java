package com.doosan.dframe.core.employee;

import com.doosan.dframe.core.department.Department;
import com.doosan.dframe.core.department.DepartmentRepository;
import com.doosan.dframe.core.role.Role;
import com.doosan.dframe.core.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return employeeRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll(Pageable.ofSize(100)).stream()
                .map(EmployeeDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getEmployeesByDepartment(String departmentCode) {
        return employeeRepository.findByDepartmentCode(departmentCode).stream()
                .map(EmployeeDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> searchEmployees(String keyword) {
        return employeeRepository.searchByKeyword(keyword).stream()
                .map(EmployeeDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployeeById(String id) {
        return employeeRepository.findById(id)
                .map(EmployeeDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeCreateRequest request) {
        if (employeeRepository.existsById(request.id())) {
            throw new RuntimeException("Username already exists");
        }
        if (employeeRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        Department department = null;
        if (request.deptCode() != null) {
            department = departmentRepository.findById(request.deptCode())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
        }

        Department dispatchDepartment = null;
        if (request.dispatchDeptCode() != null) {
            dispatchDepartment = departmentRepository.findById(request.dispatchDeptCode())
                    .orElseThrow(() -> new RuntimeException("Dispatch Department not found"));
        }

        Department workDepartment = null;
        if (request.workDeptCode() != null) {
            workDepartment = departmentRepository.findById(request.workDeptCode())
                    .orElseThrow(() -> new RuntimeException("Work Department not found"));
        }

        Employee employee = Employee.builder()
                .id(request.id())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .department(department)
                .dispatchDepartment(dispatchDepartment)
                .workDepartment(workDepartment)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .lastPasswordChangedAt(LocalDateTime.now())
                .build();

        Employee savedEmployee = employeeRepository.save(employee);

        if (request.roleCodes() != null) {
            for (String roleCode : request.roleCodes()) {
                Role role = roleRepository.findById(roleCode)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleCode));
                EmployeeRole employeeRole = EmployeeRole.builder()
                        .employee(savedEmployee)
                        .role(role)
                        .build();
                savedEmployee.getEmployeeRoles().add(employeeRole);
            }
        }

        return EmployeeDto.fromEntity(savedEmployee);
    }

    @Transactional
    public EmployeeDto updateEmployee(String id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (request.email() != null && !request.email().equals(employee.getEmail())
                && employeeRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        Department department = employee.getDepartment();
        if (request.deptCode() != null) {
            department = departmentRepository.findById(request.deptCode())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
        }

        Department dispatchDepartment = employee.getDispatchDepartment();
        if (request.dispatchDeptCode() != null) {
            dispatchDepartment = departmentRepository.findById(request.dispatchDeptCode())
                    .orElseThrow(() -> new RuntimeException("Dispatch Department not found"));
        }

        Department workDepartment = employee.getWorkDepartment();
        if (request.workDeptCode() != null) {
            workDepartment = departmentRepository.findById(request.workDeptCode())
                    .orElseThrow(() -> new RuntimeException("Work Department not found"));
        }

        employee.update(
                request.name(),
                request.englishName(),
                request.email(),
                request.phone(),
                request.position(),
                department,
                dispatchDepartment,
                workDepartment);

        employee.updateStatus(request.enabled());
        employee.updateAuthStatus(
                request.accountNonExpired(),
                request.credentialsNonExpired(),
                request.accountNonLocked(),
                request.countLoginFail()
        );

        if (request.roleCodes() != null) {
            employee.getEmployeeRoles().clear();
            for (String roleCode : request.roleCodes()) {
                Role role = roleRepository.findById(roleCode)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleCode));
                EmployeeRole employeeRole = EmployeeRole.builder()
                        .employee(employee)
                        .role(role)
                        .build();
                employee.getEmployeeRoles().add(employeeRole);
            }
        }

        return EmployeeDto.fromEntity(employee);
    }

    // Add delete method as needed
}
