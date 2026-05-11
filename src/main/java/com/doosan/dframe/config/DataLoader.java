package com.doosan.dframe.config;

import com.doosan.dframe.admin.authority.Authority;
import com.doosan.dframe.admin.authority.AuthorityRepository;
import com.doosan.dframe.admin.department.Department;
import com.doosan.dframe.admin.department.DepartmentRepository;
import com.doosan.dframe.admin.employee.Employee;
import com.doosan.dframe.admin.employee.EmployeeRepository;
import com.doosan.dframe.admin.menu.Menu;
import com.doosan.dframe.admin.menu.MenuRepository;
import com.doosan.dframe.admin.role.Role;
import com.doosan.dframe.admin.role.RoleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentRepository departmentRepository;
    private final MenuRepository menuRepository;
    private final AuthorityRepository authorityRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (employeeRepository.count() > 0) {
            return;
        }

        System.out.println("JSON 파일에서 샘플 데이터를 로드합니다 (신규 필드 포함)...");

        // 1. 메뉴 및 권한 로드
        List<MenuDto> menuDtos = loadJson("data/menus.json", new TypeReference<List<MenuDto>>() {});
        List<Authority> allAuthorities = new ArrayList<>();
        Map<Long, Menu> menuMap = new HashMap<>();

        for (MenuDto md : menuDtos) {
            Menu menu = new Menu();
            menu.setTitle(md.getTitle());
            menu.setUrl(md.getUrl());
            menu.setIcon(md.getIcon());
            menu.setSortOrder(md.getSortOrder());
            
            if (md.getParentId() != null) {
                menu.setParent(menuMap.get(md.getParentId()));
            }
            menuRepository.save(menu);
            
            if (md.getId() != null) {
                menuMap.put(md.getId(), menu);
            }

            if (md.getCode() != null && !md.getCode().isEmpty()) {
                String[] authTypes = {"READ", "EDIT", "PRINT"};
                String[] authNames = {"읽기", "편집", "출력"};
                for (int j = 0; j < authTypes.length; j++) {
                    Authority auth = new Authority();
                    auth.setName(authTypes[j] + "_" + md.getCode());
                    auth.setDescription(md.getTitle() + " " + authNames[j] + " 권한");
                    allAuthorities.add(authorityRepository.save(auth));
                }
            }
        }

        // 2. 역할 생성
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole.setDescription("시스템 최고 관리자");
        adminRole.getAuthorities().addAll(allAuthorities);
        roleRepository.save(adminRole);

        Role managerRole = new Role();
        managerRole.setName("ROLE_MANAGER");
        managerRole.setDescription("부서 관리자");
        allAuthorities.stream().filter(a -> a.getName().startsWith("READ") || a.getName().startsWith("EDIT")).forEach(managerRole.getAuthorities()::add);
        roleRepository.save(managerRole);

        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        userRole.setDescription("일반 사용자");
        allAuthorities.stream().filter(a -> a.getName().startsWith("READ")).forEach(userRole.getAuthorities()::add);
        roleRepository.save(userRole);

        Map<String, Role> roleMap = new HashMap<>();
        roleMap.put("ROLE_ADMIN", adminRole);
        roleMap.put("ROLE_MANAGER", managerRole);
        roleMap.put("ROLE_USER", userRole);

        // 3. 부서 로드 (leader는 아직 User가 없으므로 나중에 맵핑)
        List<DepartmentDto> deptDtos = loadJson("data/departments.json", new TypeReference<List<DepartmentDto>>() {});
        Map<Long, Department> deptMap = new HashMap<>();
        Map<Department, String> deptLeaderMap = new HashMap<>();
        
        for (DepartmentDto dto : deptDtos) {
            Department d = new Department();
            d.setName(dto.getName());
            d.setEnglishName(dto.getEnglishName());
            if (dto.getParentId() != null) {
                d.setParent(deptMap.get(dto.getParentId()));
            }
            departmentRepository.save(d);
            deptMap.put(dto.getId(), d);
            
            if (dto.getLeaderUsername() != null) {
                deptLeaderMap.put(d, dto.getLeaderUsername());
            }
        }

        // 4. 사용자 로드
        List<EmployeeDto> userDtos = loadJson("data/employees.json", new TypeReference<List<EmployeeDto>>() {});
        String defaultPasswordHash = passwordEncoder.encode("user123");
        String adminPasswordHash = passwordEncoder.encode("admin123");
        
        List<Employee> users = new ArrayList<>();
        Map<String, Employee> userMap = new HashMap<>();
        
        for (EmployeeDto dto : userDtos) {
            Employee u = new Employee();
            u.setUsername(dto.getUsername());
            u.setName(dto.getName());
            u.setEnglishName(dto.getEnglishName());
            u.setJobTitle(dto.getJobTitle());
            
            if ("admin".equals(dto.getUsername())) {
                u.setPassword(adminPasswordHash);
            } else {
                u.setPassword(defaultPasswordHash);
            }
            
            if (dto.getDepartmentId() != null) {
                u.setDepartment(deptMap.get(dto.getDepartmentId()));
            }
            if (dto.getWorkingDepartmentId() != null) {
                u.setWorkingDepartment(deptMap.get(dto.getWorkingDepartmentId()));
            }
            if (dto.getDispatchedDepartmentId() != null) {
                u.setDispatchedDepartment(deptMap.get(dto.getDispatchedDepartmentId()));
            }
            
            if (dto.getRole() != null) {
                u.getRoles().add(roleMap.get(dto.getRole()));
            }
            
            users.add(u);
            userMap.put(u.getUsername(), u);
            
            if (users.size() >= 1000) {
                employeeRepository.saveAll(users);
                users.clear();
            }
        }
        if (!users.isEmpty()) {
            employeeRepository.saveAll(users);
        }

        // 5. 부서 리더 맵핑 (User 저장 완료 후)
        List<Department> deptsToUpdate = new ArrayList<>();
        for (Map.Entry<Department, String> entry : deptLeaderMap.entrySet()) {
            Employee leader = userMap.get(entry.getValue());
            if (leader != null) {
                entry.getKey().setLeader(leader);
                deptsToUpdate.add(entry.getKey());
            }
        }
        if (!deptsToUpdate.isEmpty()) {
            departmentRepository.saveAll(deptsToUpdate);
        }

        System.out.println("JSON 샘플 데이터 로드 완료 (신규 필드 및 리더 맵핑 완료).");
    }

    private <T> T loadJson(String path, TypeReference<T> typeRef) throws Exception {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            return objectMapper.readValue(is, typeRef);
        }
    }

    @Data
    static class MenuDto {
        private Long id;
        private Long parentId;
        private String code;
        private String title;
        private String url;
        private String icon;
        private int sortOrder;
    }

    @Data
    static class DepartmentDto {
        private Long id;
        private String name;
        private String englishName;
        private Long parentId;
        private String leaderUsername;
    }

    @Data
    static class EmployeeDto {
        private String username;
        private String name;
        private String englishName;
        private String password;
        private Long departmentId;
        private Long workingDepartmentId;
        private Long dispatchedDepartmentId;
        private String jobTitle;
        private String role;
    }
}
