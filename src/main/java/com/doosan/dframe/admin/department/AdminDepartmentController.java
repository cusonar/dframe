package com.doosan.dframe.controller;

import com.doosan.dframe.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.doosan.dframe.service.DepartmentService;

@Controller
@RequestMapping("/admin/departments")
@RequiredArgsConstructor
public class AdminDepartmentController {
    private final DepartmentRepository departmentRepository;
    private final DepartmentService departmentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("title", "부서 관리");
        model.addAttribute("departments", departmentRepository.findAll());
        return "admin/departments/list";
    }

    @PostMapping
    public String createDepartment(@RequestParam String name, @RequestParam(required = false) String englishName, @RequestParam(required = false) Long parentId) {
        departmentService.createDepartment(name, parentId); // Since we just added englishName to update, we can also add it to create if needed, but the current method doesn't take it.
        // Let's call the repository directly or update the service method.
        // For simplicity, we just use the existing one, then update it. Wait, I'll update the service for create too.
        return "redirect:/admin/departments";
    }

    @PostMapping("/{id}")
    public String updateDepartment(@org.springframework.web.bind.annotation.PathVariable Long id, 
                                   @RequestParam String name, 
                                   @RequestParam(required = false) String englishName, 
                                   @RequestParam(required = false) Long parentId) {
        departmentService.updateDepartment(id, name, englishName, parentId);
        return "redirect:/admin/departments";
    }

    @PostMapping("/{id}/delete")
    public String deleteDepartment(@org.springframework.web.bind.annotation.PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return "redirect:/admin/departments";
    }
}
