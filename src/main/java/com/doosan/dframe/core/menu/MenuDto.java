package com.example.baseb.common.menu;

import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record MenuDto(
                String code,
                String name,
                String url,
                String icon,
                String parentCode,
                List<String> roleCodes,
                boolean visible,
                Integer sortOrder,
                List<MenuDto> children) {
        public static MenuDto fromEntity(Menu m) {
                return MenuDto.builder()
                                .code(m.getCode())
                                .name(m.getName())
                                .url(m.getUrl())
                                .icon(m.getIcon())
                                .parentCode(m.getParent() != null ? m.getParent().getCode() : null)
                                .roleCodes(m.getMenuRoles() != null
                                                ? m.getMenuRoles().stream().map(mr -> mr.getRole().getCode())
                                                                .collect(Collectors.toList())
                                                : List.of())
                                .visible(m.isVisible())
                                .sortOrder(m.getSortOrder())
                                .children(m.getChildren().stream().map(MenuDto::fromEntity)
                                                .collect(Collectors.toList()))
                                .build();
        }
}
