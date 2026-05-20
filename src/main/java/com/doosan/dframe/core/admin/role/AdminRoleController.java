package com.doosan.dframe.core.admin.role;

import com.doosan.dframe.core.role.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {
    private final RoleService roleService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("title", "역할 및 권한 관리");
        model.addAttribute("roles", roleService.getRoles());
        model.addAttribute("authorities", roleService.getAuthorities());
        return "admin/roles/list";
    }
}
