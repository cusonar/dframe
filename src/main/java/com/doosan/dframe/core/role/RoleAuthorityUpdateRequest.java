package com.example.baseb.common.role;

import java.util.List;

public record RoleAuthorityUpdateRequest(
        List<String> authorityCodes) {
}
