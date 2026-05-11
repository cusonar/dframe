package com.doosan.dframe.service;

import com.doosan.dframe.domain.Department;
import com.doosan.dframe.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public void createDepartment(String name, Long parentId) {
        Department dept = new Department();
        dept.setName(name);
        
        if (parentId != null) {
            departmentRepository.findById(parentId).ifPresent(dept::setParent);
        }
        
        departmentRepository.save(dept);
    }

    public void updateDepartment(Long id, String name, String englishName, Long parentId) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid department Id:" + id));
        
        dept.setName(name);
        dept.setEnglishName(englishName);
        
        if (parentId != null && !parentId.equals(id)) { // Prevent self-referencing
            departmentRepository.findById(parentId).ifPresent(dept::setParent);
        } else if (parentId == null) {
            dept.setParent(null);
        }
        
        departmentRepository.save(dept);
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
}
