package com.doosan.dframe.core.admin.role;

import com.doosan.dframe.core.authority.AuthorityRepository;
import com.doosan.dframe.core.role.RoleRepository;
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
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final RoleService roleService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("title", "역할 및 권한 관리");
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("authorities", authorityRepository.findAll());
        return "admin/roles/list";
    }
}
