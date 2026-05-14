package com.doosan.dframe.core.menu;

import com.doosan.dframe.core.role.Role;
import com.doosan.dframe.core.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<MenuDto> getMyMenus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Get all visible menus first
        List<Menu> allMenus = menuRepository.findByIsVisibleTrueOrderBySortOrderAsc();

        // Filter by permissions and transform to DTO
        // Note: The structure needs to be maintained.
        // A better approach for tree: Get root nodes, then filter recursively.
        // But for simplicity in this template, let's assume we can fetch whole tree and
        // filter.

        // Actually, fetching roots and traversing is safer for DTO conversion.
        List<Menu> roots = menuRepository.findByParentIsNullOrderBySortOrderAsc();

        return roots.stream()
                .filter(menu -> hasAccess(menu, authorities))
                .map(menu -> convertToDtoFiltered(menu, authorities))
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MenuDto> getAllMenus() {
        List<Menu> roots = menuRepository.findByParentIsNullOrderBySortOrderAsc();
        return roots.stream()
                .map(MenuDto::fromEntity)
                .collect(Collectors.toList());
    }

    private boolean hasAccess(Menu menu, Set<String> authorities) {
        if (!menu.isVisible())
            return false;
        if (menu.getMenuRoles() == null || menu.getMenuRoles().isEmpty()) {
            return true;
        }
        for (MenuRole menuRole : menu.getMenuRoles()) {
            if (authorities.contains(menuRole.getRole().getCode())) {
                return true;
            }
        }
        return false;
    }

    private MenuDto convertToDtoFiltered(Menu menu, Set<String> authorities) {
        if (!hasAccess(menu, authorities)) {
            return null;
        }

        List<MenuDto> children = menu.getChildren().stream()
                .filter(child -> hasAccess(child, authorities))
                .map(child -> convertToDtoFiltered(child, authorities))
                .filter(dto -> dto != null)
                .collect(Collectors.toList());

        return MenuDto.builder()
                .code(menu.getCode())
                .name(menu.getName())
                .url(menu.getUrl())
                .icon(menu.getIcon())
                .parentCode(menu.getParent() != null ? menu.getParent().getCode() : null)
                .roleCodes(menu.getMenuRoles() != null
                        ? menu.getMenuRoles().stream().map(mr -> mr.getRole().getCode()).collect(Collectors.toList())
                        : List.of())
                .visible(menu.isVisible())
                .sortOrder(menu.getSortOrder())
                .children(children)
                .build();
    }

    @Transactional
    public MenuDto updateMenuRoles(String menuCode, MenuRoleUpdateRequest request) {
        Menu menu = menuRepository.findById(menuCode)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + menuCode));

        menu.getMenuRoles().clear();

        if (request.roleCodes() != null && !request.roleCodes().isEmpty()) {
            for (String roleCode : request.roleCodes()) {
                Role role = roleRepository.findByCode(roleCode)
                        .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleCode));

                MenuRole menuRole = MenuRole.builder()
                        .menu(menu)
                        .role(role)
                        .build();
                menu.getMenuRoles().add(menuRole);
            }
        }
        return MenuDto.fromEntity(menu);
    }

    @Transactional
    public MenuDto createMenu(MenuCreateRequest request) {
        if (menuRepository.existsById(request.code())) {
            throw new IllegalArgumentException("Menu with this code already exists: " + request.code());
        }

        Menu parent = null;
        if (request.parentCode() != null && !request.parentCode().isEmpty()) {
            parent = menuRepository.findById(request.parentCode())
                    .orElseThrow(() -> new IllegalArgumentException("Parent menu not found: " + request.parentCode()));
        }

        Menu menu = Menu.builder()
                .code(request.code())
                .name(request.name())
                .url(request.url())
                .icon(request.icon())
                .parent(parent)
                .isVisible(request.visible() != null ? request.visible() : true)
                .sortOrder(request.sortOrder() != null ? request.sortOrder() : 0)
                .build();

        menu = menuRepository.save(menu);
        return MenuDto.fromEntity(menu);
    }

    @Transactional
    public MenuDto updateMenu(String menuCode, MenuUpdateRequest request) {
        Menu menu = menuRepository.findById(menuCode)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found: " + menuCode));

        Menu parent = null;
        if (request.parentCode() != null && !request.parentCode().isEmpty()) {
            parent = menuRepository.findById(request.parentCode())
                    .orElseThrow(() -> new IllegalArgumentException("Parent menu not found: " + request.parentCode()));
        }

        // We cannot update the code (primary key) easily, so we update other fields
        menu = Menu.builder()
                .code(menu.getCode())
                .name(request.name())
                .url(request.url())
                .icon(request.icon())
                .parent(parent)
                .isVisible(request.visible() != null ? request.visible() : true)
                .sortOrder(request.sortOrder() != null ? request.sortOrder() : 0)
                .children(menu.getChildren()) // preserve children
                .menuRoles(menu.getMenuRoles()) // preserve roles
                .build();

        menu = menuRepository.save(menu);
        return MenuDto.fromEntity(menu);
    }

    @Transactional
    public void deleteMenu(String menuCode) {
        if (!menuRepository.existsById(menuCode)) {
            throw new IllegalArgumentException("Menu not found: " + menuCode);
        }
        menuRepository.deleteById(menuCode);
    }
}
