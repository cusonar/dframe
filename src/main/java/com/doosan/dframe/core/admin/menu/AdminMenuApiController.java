package com.doosan.dframe.core.admin.menu;

import com.doosan.dframe.core.menu.*;
import com.doosan.dframe.core.util.TreeGridWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminMenuApiController {

    private final MenuService menuService;

    @GetMapping("/menus")
    public ResponseEntity<?> getAllMenus(
            @RequestParam(defaultValue = "false") boolean treeGrid
    ) {
        if (treeGrid) return ResponseEntity.ok(new TreeGridWrapper<>(menuService.getAllMenus()));
        return ResponseEntity.ok(menuService.getAllMenus());
    }

    @GetMapping("/menus/{menuCode}")
    public ResponseEntity<?> getMenu(@PathVariable("menuCode") String menuCode) {
        return ResponseEntity.ok(menuService.getMenu(menuCode));
    }

    @PutMapping("/menus/{menuCode}/roles")
    public ResponseEntity<MenuDto> updateMenuRoles(
            @PathVariable("menuCode") String menuCode,
            @RequestBody MenuRoleUpdateRequest request) {
        return ResponseEntity.ok(menuService.updateMenuRoles(menuCode, request));
    }

    @PostMapping("/menus")
    public ResponseEntity<MenuDto> createMenu(@RequestBody MenuCreateRequest request) {
        return ResponseEntity.ok(menuService.createMenu(request));
    }

    @PutMapping("/menus/{menuCode}")
    public ResponseEntity<MenuDto> updateMenu(
            @PathVariable("menuCode") String menuCode,
            @RequestBody MenuUpdateRequest request) {
        return ResponseEntity.ok(menuService.updateMenu(menuCode, request));
    }

    @DeleteMapping("/menus/{menuCode}")
    public ResponseEntity<Void> deleteMenu(@PathVariable("menuCode") String menuCode) {
        menuService.deleteMenu(menuCode);
        return ResponseEntity.noContent().build();
    }
}
