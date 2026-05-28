package com.doosan.dframe.core.employee;

import com.doosan.dframe.core.department.Department;
import com.doosan.dframe.core.department.DepartmentRepository;
import com.doosan.dframe.core.role.Role;
import com.doosan.dframe.core.role.RoleRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        return employeeRepository.findAll().stream()
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
    public List<EmployeeDto> searchWithFilters(List<SearchFilter> filters) {
        Specification<Employee> spec = buildSpecification(filters);
        return employeeRepository.findAll(spec).stream()
                .map(EmployeeDto::fromEntity)
                .toList();
    }

    private Specification<Employee> buildSpecification(List<SearchFilter> filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // 부서 join (LEFT JOIN)
            Join<Employee, Department> deptJoin = root.join("department", JoinType.LEFT);

            for (SearchFilter f : filters) {
                if (f.field() == null || f.operator() == null || f.value() == null || f.value().isBlank()) continue;

                String field = f.field();
                String op = f.operator();
                String val = f.value();

                try {
                    Predicate predicate = switch (op) {
                        case "equals" -> buildEquals(cb, root, deptJoin, field, val);
                        case "starts" -> buildLike(cb, root, deptJoin, field, val + "%");
                        case "ends" -> buildLike(cb, root, deptJoin, field, "%" + val);
                        case "contains" -> buildLike(cb, root, deptJoin, field, "%" + val + "%");
                        case "gt" -> buildNumberCompare(cb, root, field, val, true, false);
                        case "lt" -> buildNumberCompare(cb, root, field, val, false, true);
                        case "between" -> buildBetween(cb, root, field, val, f.value2());
                        default -> null;
                    };
                    if (predicate != null) predicates.add(predicate);
                } catch (Exception ignored) {
                    // 파싱 오류는 조건 무시
                }
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Path<?> resolvePath(Root<Employee> root, Join<Employee, Department> deptJoin, String field) {
        if (field.equals("deptName")) return deptJoin.get("name");
        if (field.equals("deptCode")) return deptJoin.get("code");
        return root.get(field);
    }

    @SuppressWarnings("unchecked")
    private Predicate buildEquals(CriteriaBuilder cb, Root<Employee> root, Join<Employee, Department> deptJoin, String field, String val) {
        Path<?> path = resolvePath(root, deptJoin, field);
        Class<?> javaType = path.getJavaType();
        if (javaType == boolean.class || javaType == Boolean.class) {
            return cb.equal(path, Boolean.parseBoolean(val));
        }
        if (javaType == int.class || javaType == Integer.class) {
            return cb.equal(path, Integer.parseInt(val));
        }
        return cb.equal(cb.lower((Path<String>) path), val.toLowerCase());
    }

    @SuppressWarnings("unchecked")
    private Predicate buildLike(CriteriaBuilder cb, Root<Employee> root, Join<Employee, Department> deptJoin, String field, String pattern) {
        Path<String> path = (Path<String>) resolvePath(root, deptJoin, field);
        return cb.like(cb.lower(path), pattern.toLowerCase());
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate buildNumberCompare(CriteriaBuilder cb, Root<Employee> root, String field, String val, boolean gt, boolean lt) {
        Path path = root.get(field);
        int num = Integer.parseInt(val);
        if (gt) return cb.greaterThan(path, num);
        return cb.lessThan(path, num);
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate buildBetween(CriteriaBuilder cb, Root<Employee> root, String field, String val, String val2) {
        Path path = root.get(field);
        Class<?> javaType = path.getJavaType();
        if (javaType == LocalDateTime.class) {
            LocalDateTime from = LocalDate.parse(val).atStartOfDay();
            LocalDateTime to = val2 != null && !val2.isBlank()
                    ? LocalDate.parse(val2).atTime(23, 59, 59)
                    : LocalDateTime.now();
            return cb.between(path, from, to);
        }
        // 숫자 between
        int from = Integer.parseInt(val);
        int to = val2 != null && !val2.isBlank() ? Integer.parseInt(val2) : Integer.MAX_VALUE;
        return cb.between(path, from, to);
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
                request.countLoginFail());

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

    @Transactional
    public void deleteEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        employeeRepository.delete(employee);
    }
}
