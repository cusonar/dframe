package com.example.baseb.common.department;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public List<DepartmentDto> getDepartmentTree() {
        return departmentRepository.findByParentIsNullOrderBySortOrderAsc().stream()
                .map(DepartmentDto::fromEntity)
                .collect(Collectors.toList());
    }
}
