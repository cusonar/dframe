package com.doosan.dframe.core.role;

import java.util.List;

public record RoleAuthorityUpdateRequest(
        List<String> authorityCodes) {
}
