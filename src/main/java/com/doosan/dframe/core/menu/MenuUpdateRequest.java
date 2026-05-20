package com.doosan.dframe.core.menu;

import java.util.Set;

public record MenuUpdateRequest(
        String name,
        String url,
        String icon,
        String parentCode,
        Boolean visible,
        Integer sortOrder,
        Set<String> roleCodes) {
}
