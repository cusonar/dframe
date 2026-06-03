package com.doosan.dframe.core.admin.employee;

import com.doosan.dframe.core.role.RoleDto;
import com.doosan.dframe.core.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminEmployeeController {

    private final RoleService roleService;

    @GetMapping("/employees")
    public String list(Model model) {
        List<RoleDto> roles = roleService.getRoles();
        model.addAttribute("roles", roles);
        return "admin/employees/list";
    }
}
