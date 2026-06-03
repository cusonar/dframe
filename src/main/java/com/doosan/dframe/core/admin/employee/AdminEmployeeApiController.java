package com.doosan.dframe.core.admin.employee;

import com.doosan.dframe.core.employee.EmployeeCreateRequest;
import com.doosan.dframe.core.employee.EmployeeDto;
import com.doosan.dframe.core.employee.EmployeeService;
import com.doosan.dframe.core.employee.EmployeeUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminEmployeeApiController {

    private final EmployeeService employeeService;
    private final tools.jackson.databind.json.JsonMapper objectMapper;

    @PostMapping("/employees")
    public ResponseEntity<EmployeeDto> createEmployee(@Valid @RequestBody EmployeeCreateRequest request) {
        return ResponseEntity.ok(employeeService.createEmployee(request));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable String id,
            @Valid @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @PostMapping("/employees/save")
    public ResponseEntity<?> saveEmployees(jakarta.servlet.http.HttpServletRequest httpRequest) {
        TreeGridSaveRequest request = null;
        try {
            String contentType = httpRequest.getContentType();
            if (contentType != null && contentType.contains("application/json")) {
                request = objectMapper.readValue(httpRequest.getInputStream(), TreeGridSaveRequest.class);
            } else {
                String tgData = httpRequest.getParameter("TGData");
                String data = httpRequest.getParameter("Data");
                String jsonData = tgData != null ? tgData : data;
                if (jsonData != null && !jsonData.isBlank()) {
                    request = objectMapper.readValue(jsonData, TreeGridSaveRequest.class);
                }
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> io = new HashMap<>();
            io.put("Result", -1);
            io.put("Message", "데이터 수신 및 해석 실패: " + e.getMessage());
            response.put("IO", io);
            return ResponseEntity.ok(response);
        }

        if (request == null || request.Changes() == null) {
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> io = new HashMap<>();
            io.put("Result", -1);
            io.put("Message", "저장할 변경 사항 데이터가 없습니다.");
            response.put("IO", io);
            return ResponseEntity.ok(response);
        }

        try {
            if (request.Changes() != null) {
                for (TreeGridChangeItem item : request.Changes()) {
                    if (item.Deleted() != null && item.Deleted() == 1) {
                        employeeService.deleteEmployee(item.id());
                    } else if (item.Added() != null && item.Added() == 1) {
                        List<String> roles = item.roleCodes() != null && !item.roleCodes().isBlank()
                                ? List.of(item.roleCodes().split(";"))
                                : List.of("ROLE_USER");
                        EmployeeCreateRequest createRequest = new EmployeeCreateRequest(
                                item.id(),
                                "admin123", // 기본 패스워드
                                item.name(),
                                item.email(),
                                item.phone(),
                                item.deptCode(),
                                item.dispatchDeptCode(),
                                item.workDeptCode(),
                                roles
                        );
                        employeeService.createEmployee(createRequest);
                    } else if (item.Changed() != null && item.Changed() == 1) {
                        List<String> roles = null;
                        if (item.roleCodes() != null) {
                            roles = item.roleCodes().isBlank()
                                    ? List.of()
                                    : List.of(item.roleCodes().split(";"));
                        }
                        EmployeeUpdateRequest updateRequest = new EmployeeUpdateRequest(
                                item.name(),
                                item.englishName(),
                                item.email(),
                                item.phone(),
                                item.position(),
                                item.deptCode(),
                                item.dispatchDeptCode(),
                                item.workDeptCode(),
                                roles,
                                parseBoolean(item.enabled()),
                                parseBoolean(item.accountNonExpired()),
                                parseBoolean(item.credentialsNonExpired()),
                                parseBoolean(item.accountNonLocked()),
                                item.countLoginFail()
                        );
                        employeeService.updateEmployee(item.id(), updateRequest);
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            Map<String, Object> io = new HashMap<>();
            io.put("Result", 0);
            io.put("Message", ""); // 빈 문자열로 처리하여 TreeGrid 내부의 alert 팝업 트리거 방지
            response.put("IO", io);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> io = new HashMap<>();
            io.put("Result", -1);
            io.put("Message", ""); // 에러 시에도 alert 팝업을 방지하기 위해 빈 문자열로 처리
            io.put("ErrorMsg", e.getMessage()); // 커스텀 에러 필드에 오류 원인 기록
            response.put("IO", io);
            return ResponseEntity.ok(response);
        }
    }

    private static Boolean parseBoolean(Object value) {
        if (value == null) return null;
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }
        String str = value.toString().trim();
        return "1".equals(str) || "true".equalsIgnoreCase(str);
    }

    public static record TreeGridSaveRequest(
            List<TreeGridChangeItem> Changes
    ) {}

    public static record TreeGridChangeItem(
            String id,
            String name,
            String englishName,
            String email,
            String phone,
            String position,
            String deptCode,
            String dispatchDeptCode,
            String workDeptCode,
            String roleCodes,
            Object enabled,
            Object accountNonExpired,
            Object credentialsNonExpired,
            Object accountNonLocked,
            Integer countLoginFail,
            Integer Added,
            Integer Changed,
            Integer Deleted
    ) {}
}
