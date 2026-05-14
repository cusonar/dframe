package com.doosan.dframe.core.authority;

import lombok.Builder;

@Builder
public record AuthorityDto(
        String code,
        String description) {
    public static AuthorityDto from(Authority authority) {
        if (authority == null) {
            return null;
        }
        return AuthorityDto.builder()
                .code(authority.getCode())
                .description(authority.getDescription())
                .build();
    }
}
