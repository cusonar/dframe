package com.doosan.dframe.core.menu;

public record MenuUpdateRequest(
        String name,
        String url,
        String icon,
        String parentCode,
        Boolean visible,
        Integer sortOrder) {
}
