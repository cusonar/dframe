package com.doosan.dframe.admin.menu;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {
    private final MenuRepository menuRepository;

    public void createMenu(String title, String url, String icon, int sortOrder, Long parentId, String requiredRole) {
        Menu menu = new Menu();
        menu.setTitle(title);
        menu.setUrl(url);
        menu.setIcon(icon);
        menu.setSortOrder(sortOrder);
        menu.setRequiredRole(requiredRole);

        if (parentId != null) {
            menuRepository.findById(parentId).ifPresent(menu::setParent);
        }

        menuRepository.save(menu);
    }

    public void updateMenu(Long id, String title, String url, String icon, int sortOrder, Long parentId,
            String requiredRole) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id:" + id));

        menu.setTitle(title);
        menu.setUrl(url);
        menu.setIcon(icon);
        menu.setSortOrder(sortOrder);
        menu.setRequiredRole(requiredRole);

        if (parentId != null && !parentId.equals(id)) {
            menuRepository.findById(parentId).ifPresent(menu::setParent);
        } else if (parentId == null) {
            menu.setParent(null);
        }

        menuRepository.save(menu);
    }

    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }
}
