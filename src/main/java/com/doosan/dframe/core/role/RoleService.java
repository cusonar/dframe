package com.doosan.dframe.core.role;

import com.doosan.dframe.core.authority.Authority;
import com.doosan.dframe.core.authority.AuthorityCreateRequest;
import com.doosan.dframe.core.authority.AuthorityDto;
import com.doosan.dframe.core.authority.AuthorityRepository;
import com.doosan.dframe.core.menu.Menu;
import com.doosan.dframe.core.menu.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final MenuRepository menuRepository;

    @Transactional(readOnly = true)
    public List<RoleDto> getRoles() {
        return roleRepository.findAll().stream()
                .map(RoleDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuthorityDto> getAuthorities() {
        return authorityRepository.findAll().stream()
                .map(AuthorityDto::from)
                .toList();
    }

    @Transactional
    public RoleDto updateRoleAuthorities(String roleCode, RoleAuthorityUpdateRequest request) {
        Role role = roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleCode));

        role.getRoleAuthorities().clear();

        if (request.authorityCodes() != null && !request.authorityCodes().isEmpty()) {
            List<Authority> authorities = authorityRepository.findAllById(request.authorityCodes());
            if (authorities.size() != request.authorityCodes().size()) {
                throw new IllegalArgumentException("Some authority codes are invalid");
            }

            for (Authority authority : authorities) {
                RoleAuthority roleAuthority = RoleAuthority.builder()
                        .role(role)
                        .authority(authority)
                        .build();
                role.getRoleAuthorities().add(roleAuthority);
            }
        }

        return RoleDto.from(role);
    }

    @Transactional
    public RoleDto createRole(RoleCreateRequest request) {
        if (roleRepository.existsById(request.code())) {
            throw new IllegalArgumentException("Role already exists with code: " + request.code());
        }

        Role role = Role.builder()
                .code(request.code())
                .description(request.description())
                .build();

        Role savedRole = roleRepository.save(role);
        return RoleDto.from(savedRole);
    }

    @Transactional
    public AuthorityDto createAuthority(AuthorityCreateRequest request) {
        if (authorityRepository.existsById(request.code())) {
            throw new IllegalArgumentException("Authority already exists with code: " + request.code());
        }

        Authority authority = Authority.builder()
                .code(request.code())
                .description(request.description())
                .build();

        Authority savedAuthority = authorityRepository.save(authority);
        return AuthorityDto.from(savedAuthority);
    }

    @Transactional
    public void deleteRole(String roleCode) {
        Role role = roleRepository.findByCode(roleCode)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleCode));

        // Remove menu roles associated with this role to avoid foreign key constraints
        List<Menu> menus = menuRepository.findAll();
        for (Menu menu : menus) {
            menu.getMenuRoles().removeIf(mr -> mr.getRole().getCode().equals(roleCode));
        }
        menuRepository.saveAll(menus);

        roleRepository.delete(role);
    }

    @Transactional
    public void deleteAuthority(String authorityCode) {
        Authority authority = authorityRepository.findById(authorityCode)
                .orElseThrow(() -> new IllegalArgumentException("Authority not found: " + authorityCode));

        authorityRepository.delete(authority);
    }
}
