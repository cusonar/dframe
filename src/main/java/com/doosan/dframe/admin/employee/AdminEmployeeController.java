package com.doosan.dframe.admin.employee;

import com.doosan.dframe.admin.department.DepartmentRepository;
import com.doosan.dframe.admin.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequestMapping("/admin/employees")
@RequiredArgsConstructor
public class AdminEmployeeController {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final EmployeeService employeeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("title", "사용자 관리");
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("unassignedUsers", employeeRepository.findByDepartmentIsNull());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/employees/list";
    }

    @PostMapping
    public String createEmployee(
            @RequestParam String username,
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) List<Long> roleIds,
            @RequestParam(defaultValue = "false") boolean enabled) {

        employeeService.createEmployee(username, name, password, departmentId, roleIds, enabled);
        return "redirect:/admin/employees";
    }

    @PostMapping("/{id}")
    public String updateEmployee(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) List<Long> roleIds,
            @RequestParam(defaultValue = "false") boolean enabled) {

        employeeService.updateEmployee(id, name, password, departmentId, roleIds, enabled);
        return "redirect:/admin/employees";
    }

    @PostMapping("/{id}/delete")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/admin/employees";
    }
}
