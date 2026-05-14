package com.example.baseb.common.role;

import jakarta.validation.constraints.NotBlank;

public record RoleCreateRequest(
        @NotBlank(message = "Role code is required")
        String code,
        
        @NotBlank(message = "Role description is required")
        String description
) {
}
