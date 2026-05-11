package com.doosan.dframe.admin.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.doosan.dframe.admin.role.RoleRepository;

@Controller
@RequestMapping("/admin/menus")
@RequiredArgsConstructor
public class AdminMenuController {
    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;
    private final MenuService menuService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("title", "메뉴 관리");
        model.addAttribute("menus", menuRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/menus/list";
    }

    @PostMapping
    public String createMenu(
            @RequestParam String title,
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String icon,
            @RequestParam(defaultValue = "1") int sortOrder,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) String requiredRole) {
        menuService.createMenu(title, url, icon, sortOrder, parentId, requiredRole);
        return "redirect:/admin/menus";
    }

    @PostMapping("/{id}")
    public String updateMenu(
            @org.springframework.web.bind.annotation.PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String icon,
            @RequestParam(defaultValue = "1") int sortOrder,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) String requiredRole) {
        menuService.updateMenu(id, title, url, icon, sortOrder, parentId, requiredRole);
        return "redirect:/admin/menus";
    }

    @PostMapping("/{id}/delete")
    public String deleteMenu(@org.springframework.web.bind.annotation.PathVariable Long id) {
        menuService.deleteMenu(id);
        return "redirect:/admin/menus";
    }
}
