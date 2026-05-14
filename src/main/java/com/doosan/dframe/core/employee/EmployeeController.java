package com.example.baseb.common.employee;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/employees")
    @PreAuthorize("hasAuthority('OP_READ')")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees(
            @RequestParam(required = false) String deptCode,
            @RequestParam(required = false) String keyword) {
        if (deptCode != null && !deptCode.isEmpty()) {
            return ResponseEntity.ok(employeeService.getEmployeesByDepartment(deptCode));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            return ResponseEntity.ok(employeeService.searchEmployees(keyword));
        }
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/employees/{id}")
    @PreAuthorize("hasAuthority('OP_READ')")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping("/employees")
    @PreAuthorize("hasAuthority('OP_WRITE')")
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        return ResponseEntity.ok(employeeService.createEmployee(request));
    }

    @PutMapping("/employees/{id}")
    @PreAuthorize("hasAuthority('OP_WRITE')")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable String id,
            @Valid @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }
}
