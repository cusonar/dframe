package com.doosan.dframe.core.role;

import com.doosan.dframe.core.authority.AuthorityCreateRequest;
import com.doosan.dframe.core.authority.AuthorityDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/roles")
    public ResponseEntity<List<RoleDto>> getRoles() {
        return ResponseEntity.ok(roleService.getRoles());
    }

    @GetMapping("/authorities")
    public ResponseEntity<List<AuthorityDto>> getAuthorities() {
        return ResponseEntity.ok(roleService.getAuthorities());
    }

    @PutMapping("/roles/{roleCode}/authorities")
    public ResponseEntity<RoleDto> updateRoleAuthorities(
            @PathVariable("roleCode") String roleCode,
            @RequestBody RoleAuthorityUpdateRequest request) {
        return ResponseEntity.ok(roleService.updateRoleAuthorities(roleCode, request));
    }

    @PostMapping("/roles")
    public ResponseEntity<RoleDto> createRole(@RequestBody @Valid RoleCreateRequest request) {
        return ResponseEntity.ok(roleService.createRole(request));
    }

    @PostMapping("/authorities")
    public ResponseEntity<AuthorityDto> createAuthority(@RequestBody @Valid AuthorityCreateRequest request) {
        return ResponseEntity.ok(roleService.createAuthority(request));
    }

    @DeleteMapping("/roles/{roleCode}")
    public ResponseEntity<Void> deleteRole(@PathVariable("roleCode") String roleCode) {
        roleService.deleteRole(roleCode);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/authorities/{authorityCode}")
    public ResponseEntity<Void> deleteAuthority(@PathVariable("authorityCode") String authorityCode) {
        roleService.deleteAuthority(authorityCode);
        return ResponseEntity.noContent().build();
    }
}
