package com.doosan.dframe.core.employee;

import com.doosan.dframe.core.util.TreeGridWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmployeeApiController {

    private final EmployeeService employeeService;

    @GetMapping("/employees")
    public ResponseEntity<?> getAllEmployees(
            @RequestParam(required = false) String deptCode,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean treeGrid) {
        if (deptCode != null && !deptCode.isEmpty()) {
            if (treeGrid)
                return ResponseEntity.ok(new TreeGridWrapper<>(employeeService.getEmployeesByDepartment(deptCode)));
            return ResponseEntity.ok(employeeService.getEmployeesByDepartment(deptCode));
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (treeGrid) return ResponseEntity.ok(new TreeGridWrapper<>(employeeService.searchEmployees(keyword)));
            return ResponseEntity.ok(employeeService.searchEmployees(keyword));
        }

        if (treeGrid) return ResponseEntity.ok(new TreeGridWrapper<>(employeeService.getAllEmployees()));
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

}
