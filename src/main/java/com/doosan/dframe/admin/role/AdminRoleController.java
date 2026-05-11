package com.doosan.dframe.admin.role;

import com.doosan.dframe.admin.authority.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    @PostMapping
    public String createRole(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam(required = false) List<Long> authorityIds) {
        roleService.createRole(name, description, authorityIds);
        return "redirect:/admin/roles";
    }
}
