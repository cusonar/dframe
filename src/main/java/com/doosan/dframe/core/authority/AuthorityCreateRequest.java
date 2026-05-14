package com.doosan.dframe.admin.authority;

import jakarta.validation.constraints.NotBlank;

public record AuthorityCreateRequest(
        @NotBlank(message = "Authority code is required")
        String code,

        @NotBlank(message = "Authority description is required")
        String description
) {
}
