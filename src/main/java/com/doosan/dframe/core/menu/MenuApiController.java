package com.doosan.dframe.core.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/menus/my")
    public ResponseEntity<List<MenuDto>> getMyMenus() {
        return ResponseEntity.ok(menuService.getMyMenus());
    }

    @GetMapping("/menus")
    public ResponseEntity<List<MenuDto>> getAllMenus() {
        return ResponseEntity.ok(menuService.getAllMenus());
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
