package com.example.baseb.common.role;

import com.example.baseb.common.authority.AuthorityDto;
import lombok.Builder;

import java.util.List;

@Builder
public record RoleDto(
        String code,
        String description,
        List<AuthorityDto> authorities) {
    public static RoleDto from(Role role) {
        if (role == null) {
            return null;
        }
        return RoleDto.builder()
                .code(role.getCode())
                .description(role.getDescription())
                .authorities(
                        role.getRoleAuthorities() != null ? role.getRoleAuthorities().stream()
                                .map(ra -> AuthorityDto.from(ra.getAuthority()))
                                .toList()
                                : List.of())
                .build();
    }
}
