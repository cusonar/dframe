package com.example.baseb.common.department;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/v1/departments")
    public ResponseEntity<List<DepartmentDto>> getDepartmentTree() {
        return ResponseEntity.ok(departmentService.getDepartmentTree());
    }
}
