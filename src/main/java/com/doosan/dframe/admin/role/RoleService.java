package com.doosan.dframe.admin.role;

import com.doosan.dframe.admin.authority.Authority;
import com.doosan.dframe.admin.authority.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;

    public void createRole(String name, String description, List<Long> authorityIds) {
        Role role = new Role();
        // 권한명은 ROLE_ 로 시작하는 것이 Spring Security 관례
        if (name != null && !name.toUpperCase().startsWith("ROLE_")) {
            name = "ROLE_" + name.toUpperCase();
        }
        role.setName(name != null ? name.toUpperCase() : null);
        role.setDescription(description);

        if (authorityIds != null && !authorityIds.isEmpty()) {
            Set<Authority> authorities = authorityIds.stream()
                .map(id -> authorityRepository.findById(id).orElse(null))
                .filter(a -> a != null)
                .collect(Collectors.toSet());
            role.setAuthorities(authorities);
        }

        roleRepository.save(role);
    }
}
