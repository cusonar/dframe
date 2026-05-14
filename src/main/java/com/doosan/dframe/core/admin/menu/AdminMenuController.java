package com.doosan.dframe.core.menu;

import com.doosan.dframe.core.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/menus")
@RequiredArgsConstructor
public class AdminMenuController {
    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;
    private final MenuService menuService;

    @GetMapping
    public String list(Model model) {
        return "admin/menus/list";
    }
}
