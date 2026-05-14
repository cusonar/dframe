package com.doosan.dframe.core.menu;

public record MenuRequest(
        String code,
        String name,
        String url,
        String icon,
        String parentCode,
        Boolean visible,
        Integer sortOrder) {
}
