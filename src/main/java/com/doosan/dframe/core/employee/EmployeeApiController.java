package com.doosan.dframe.core.employee;

import com.doosan.dframe.core.util.TreeGridWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmployeeApiController {

    private final EmployeeService employeeService;

    @GetMapping("/employees")
    public ResponseEntity<?> getAllEmployees(
            @RequestParam(required = false) String deptCode,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> filter,
            @RequestParam(defaultValue = "false") boolean treeGrid) {

        // 고급 검색 filter 파라미터 처리 (형식: "field:operator:value" 또는 "field:operator:value:value2")
        if (filter != null && !filter.isEmpty()) {
            List<SearchFilter> filters = new ArrayList<>();
            for (String f : filter) {
                String[] parts = f.split(":", 4);
                if (parts.length >= 3) {
                    String value2 = parts.length == 4 ? parts[3] : null;
                    filters.add(new SearchFilter(parts[0], parts[1], parts[2], value2));
                }
            }
            if (!filters.isEmpty()) {
                List<EmployeeDto> result = employeeService.searchWithFilters(filters);
                if (treeGrid) return ResponseEntity.ok(new TreeGridWrapper<>(result));
                return ResponseEntity.ok(result);
            }
        }

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
