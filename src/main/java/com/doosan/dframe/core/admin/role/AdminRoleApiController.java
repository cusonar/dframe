package com.doosan.dframe.core.admin.role;

import com.doosan.dframe.core.role.RoleAuthorityUpdateRequest;
import com.doosan.dframe.core.role.RoleCreateRequest;
import com.doosan.dframe.core.role.RoleDto;
import com.doosan.dframe.core.role.RoleService;
import com.doosan.dframe.core.util.TreeGridWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminRoleApiController {

    private final RoleService roleService;

    /** 역할 전체 목록 조회 */
    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles(@RequestParam(defaultValue = "false") boolean treeGrid) {
        if (treeGrid) {
            return ResponseEntity.ok(new TreeGridWrapper<>(roleService.getRoles()));
        }
        return ResponseEntity.ok(roleService.getRoles());
    }

    /** 역할 생성 */
    @PostMapping("/roles")
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleCreateRequest request) {
        return ResponseEntity.ok(roleService.createRole(request));
    }

    /** 역할에 매핑된 세부 권한 업데이트 */
    @PutMapping("/roles/{code}/authorities")
    public ResponseEntity<RoleDto> updateRoleAuthorities(
            @PathVariable String code,
            @RequestBody RoleAuthorityUpdateRequest request) {
        return ResponseEntity.ok(roleService.updateRoleAuthorities(code, request));
    }

    /** 역할 삭제 */
    @DeleteMapping("/roles/{code}")
    public ResponseEntity<Void> deleteRole(@PathVariable String code) {
        roleService.deleteRole(code);
        return ResponseEntity.noContent().build();
    }
}
