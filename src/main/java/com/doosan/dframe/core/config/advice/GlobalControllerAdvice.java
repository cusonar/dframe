package com.doosan.dframe.config.advice;

import com.doosan.dframe.admin.menu.Menu;
import com.doosan.dframe.admin.menu.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private final MenuRepository menuRepository;

    @ModelAttribute("globalMenus")
    public List<Menu> globalMenus() {
        // Only return root menus since Thymeleaf can traverse children
        return menuRepository.findAllByOrderBySortOrderAsc().stream()
                .filter(menu -> menu.getParent() == null)
                .collect(Collectors.toList());
    }
}
