package com.doosan.dframe.core.config;

import com.doosan.dframe.core.authority.Authority;
import com.doosan.dframe.core.authority.AuthorityRepository;
import com.doosan.dframe.core.department.Department;
import com.doosan.dframe.core.department.DepartmentRepository;
import com.doosan.dframe.core.employee.Employee;
import com.doosan.dframe.core.employee.EmployeeRepository;
import com.doosan.dframe.core.employee.EmployeeRole;
import com.doosan.dframe.core.grid.GridLayout;
import com.doosan.dframe.core.grid.GridLayoutColumn;
import com.doosan.dframe.core.grid.GridLayoutRepository;
import com.doosan.dframe.core.menu.Menu;
import com.doosan.dframe.core.menu.MenuRepository;
import com.doosan.dframe.core.menu.MenuRole;
import com.doosan.dframe.core.role.Role;
import com.doosan.dframe.core.role.RoleAuthority;
import com.doosan.dframe.core.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final EmployeeRepository employeeRepository;
    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;
    private final DepartmentRepository departmentRepository;
    private final MenuRepository menuRepository;
    private final GridLayoutRepository gridLayoutRepository;
    private final PasswordEncoder passwordEncoder;
    private final JsonMapper objectMapper;

    @org.springframework.beans.factory.annotation.Value("${app.init-data.enabled:true}")
    private boolean initDataEnabled;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!initDataEnabled) {
            log.info("Data initialization is disabled by configuration (app.init-data.enabled).");
            return;
        }

        if (employeeRepository.count() > 0) {
            log.info("Data already exists. Skipping initialization.");
            return;
        }

        log.info("Initializing sample data...");

        initAuthoritiesAndRoles();
        loadDepartments();
        loadEmployees();
        createMenus();
        initGridLayouts();

        log.info("Sample data initialized.");
    }

    private void initAuthoritiesAndRoles() {
        Authority opRead = authorityRepository.save(Authority.builder().code("OP_READ").description("Read Operation").build());
        Authority opWrite = authorityRepository.save(Authority.builder().code("OP_WRITE").description("Write Operation").build());
        Authority opDelete = authorityRepository.save(Authority.builder().code("OP_DELETE").description("Delete Operation").build());
        authorityRepository.save(Authority.builder().code("DEPT_READ").description("Read Department").build());

        Role roleAdmin = Role.builder().code("ROLE_ADMIN").description("Administrator").build();
        roleAdmin.getRoleAuthorities().add(RoleAuthority.builder().role(roleAdmin).authority(opRead).build());
        roleAdmin.getRoleAuthorities().add(RoleAuthority.builder().role(roleAdmin).authority(opWrite).build());
        roleAdmin.getRoleAuthorities().add(RoleAuthority.builder().role(roleAdmin).authority(opDelete).build());
        roleRepository.save(roleAdmin);

        Role roleUser = Role.builder().code("ROLE_USER").description("Standard User").build();
        roleUser.getRoleAuthorities().add(RoleAuthority.builder().role(roleUser).authority(opRead).build());
        roleRepository.save(roleUser);
    }

    private void loadDepartments() {
        try {
            InputStream is = new ClassPathResource("init-departments.json").getInputStream();
            List<Map<String, Object>> deptList = objectMapper.readValue(is, new TypeReference<>() {
            });

            // Sort: parents first (null parentCode comes first, then by depth)
            // Since JSON is already ordered by hierarchy (HQ -> dept -> team), just process
            // in order
            Map<String, Department> deptMap = new HashMap<>();

            for (Map<String, Object> item : deptList) {
                String code = (String) item.get("code");
                String name = (String) item.get("name");
                String englishName = (String) item.get("englishName");
                String parentCode = (String) item.get("parentCode");
                Integer sortOrder = item.get("sortOrder") != null ? ((Number) item.get("sortOrder")).intValue() : 0;

                Department parent = parentCode != null ? deptMap.get(parentCode) : null;

                Department dept = departmentRepository.save(Department.builder().code(code).name(name).englishName(englishName).parent(parent).sortOrder(sortOrder).build());
                deptMap.put(code, dept);
            }

            log.info("Loaded {} departments from init-departments.json", deptMap.size());
        } catch (Exception e) {
            log.error("Failed to load departments from JSON", e);
            throw new RuntimeException("Failed to load department data", e);
        }
    }

    private void loadEmployees() {
        try {
            InputStream is = new ClassPathResource("init-employees.json").getInputStream();
            List<Map<String, Object>> empList = objectMapper.readValue(is, new TypeReference<>() {
            });

            Map<String, Role> roleMap = new HashMap<>();
            roleRepository.findAll().forEach(r -> roleMap.put(r.getCode(), r));

            // Encode password once for all employees (they all use "admin123")
            String encodedPassword = passwordEncoder.encode("admin123");

            int count = 0;
            for (Map<String, Object> item : empList) {
                String id = (String) item.get("id");
                String name = (String) item.get("name");
                String email = (String) item.get("email");
                String phone = (String) item.get("phone");
                String englishName = (String) item.get("englishName");
                String position = (String) item.get("position");
                String deptCode = (String) item.get("deptCode");
                String dispatchDeptCode = (String) item.get("dispatchDeptCode");
                String workDeptCode = (String) item.get("workDeptCode");

                @SuppressWarnings("unchecked") List<String> roleCodes = (List<String>) item.get("roleCodes");

                Department dept = deptCode != null ? departmentRepository.findById(deptCode).orElse(null) : null;

                Department dispatchDept = dispatchDeptCode != null ? departmentRepository.findById(dispatchDeptCode).orElse(null) : null;

                Department workDept = workDeptCode != null ? departmentRepository.findById(workDeptCode).orElse(null) : null;

                Employee employee = Employee.builder().id(id).password(encodedPassword).name(name).email(email).phone(phone).englishName(englishName).position(position).department(dept).dispatchDepartment(dispatchDept).workDepartment(workDept).enabled(true).accountNonExpired(true).accountNonLocked(true).credentialsNonExpired(true).build();

                if (roleCodes != null) {
                    for (String roleCode : roleCodes) {
                        Role role = roleMap.get(roleCode);
                        if (role != null) {
                            employee.getEmployeeRoles().add(EmployeeRole.builder().employee(employee).role(role).build());
                        }
                    }
                }

                employeeRepository.save(employee);
                count++;

                if (count % 1000 == 0) {
                    log.info("Loaded {} employees...", count);
                }
            }

            log.info("Loaded {} employees from init-employees.json", count);
        } catch (Exception e) {
            log.error("Failed to load employees from JSON", e);
            throw new RuntimeException("Failed to load employee data", e);
        }
    }

    private void createMenus() {
        Role adminRole = roleRepository.findByCode("ROLE_ADMIN").orElseThrow();
        Role userRole = roleRepository.findByCode("ROLE_USER").orElseThrow();

        Menu dashboardMenu = Menu.builder().code("dashboard").name("Dashboard").url("/").icon("fa-dashboard").sortOrder(1).build();
        dashboardMenu.getMenuRoles().add(MenuRole.builder().menu(dashboardMenu).role(adminRole).build());
        dashboardMenu.getMenuRoles().add(MenuRole.builder().menu(dashboardMenu).role(userRole).build());
        menuRepository.save(dashboardMenu);

        Menu systemMenu = Menu.builder().code("system-management").name("System Management").icon("fa-cogs").sortOrder(2).build();
        systemMenu.getMenuRoles().add(MenuRole.builder().menu(systemMenu).role(adminRole).build());
        systemMenu = menuRepository.save(systemMenu);

        Menu userMgmtMenu = Menu.builder().code("employee-management").name("Employee Management").url("/admin/employees").icon("fa-users").parent(systemMenu).sortOrder(1).build();
        userMgmtMenu.getMenuRoles().add(MenuRole.builder().menu(userMgmtMenu).role(adminRole).build());
        menuRepository.save(userMgmtMenu);

        Menu roleMgmtMenu = Menu.builder().code("role-management").name("Role Management").url("/admin/roles").icon("fa-user-shield").parent(systemMenu).sortOrder(2).build();
        roleMgmtMenu.getMenuRoles().add(MenuRole.builder().menu(roleMgmtMenu).role(adminRole).build());
        menuRepository.save(roleMgmtMenu);

        Menu menuMgmtMenu = Menu.builder().code("menu-management").name("Menu Management").url("/admin/menus").icon("fa-list").parent(systemMenu).sortOrder(4).build();
        menuMgmtMenu.getMenuRoles().add(MenuRole.builder().menu(menuMgmtMenu).role(adminRole).build());
        menuRepository.save(menuMgmtMenu);

        Menu approvalMenu = Menu.builder().code("approval-management").name("Approval").url("/approvals").icon("fa-inbox").sortOrder(3).build();
        approvalMenu.getMenuRoles().add(MenuRole.builder().menu(approvalMenu).role(adminRole).build());
        approvalMenu.getMenuRoles().add(MenuRole.builder().menu(approvalMenu).role(userRole).build());
        menuRepository.save(approvalMenu);
    }

    private void initGridLayouts() {
        if (gridLayoutRepository.count() > 0) {
            log.info("Grid layouts already exist. Skipping initialization.");
            return;
        }

        // ── adminEmployeeGrid ─────────────────────────────────────────────────
        GridLayout adminEmployeeLayout = GridLayout.builder()
                .gridId("adminEmployeeGrid")
                .description("직원 관리 그리드")
                .paging(2).pageLength(50)
                .alertError(0)
                .maxHeight(1).constHeight(1)
                .maxWidth(1).constWidth(1)
                .pagerVisible(0)
                .build();

        String[] empColNames  = {"id", "name", "email", "englishName", "position", "phone",
                "deptCode", "deptName", "dispatchDeptCode", "dispatchDeptName",
                "workDeptCode", "workDeptName",
                "enabled", "accountNonExpired", "accountNonLocked", "credentialsNonExpired",
                "countLoginFail", "lastPasswordChangedAt", "roleCodes", "lastLoginAt"};
        String[] empColTypes  = {"Text", "Text", "Text", "Text", "Enum", "Text",
                "Text", "Text", "Text", "Text",
                "Text", "Text",
                "Bool", "Bool", "Bool", "Bool",
                "Int", "Date", "Html", "Date"};
        String[] empHeaders   = {"ID", "이름", "이메일", "영문명", "직급", "전화번호",
                "부서코드", "부서명", "발령부서코드", "발령부서명",
                "업무부서코드", "업무부서명",
                "활성여부", "계정만료여부", "계정잠김여부", "패스워드만료여부",
                "패스워드실패횟수", "마지막패스워드변경일시", "권한코드", "마지막로그인일시"};

        for (int i = 0; i < empColNames.length; i++) {
            GridLayoutColumn.GridLayoutColumnBuilder colBuilder = GridLayoutColumn.builder()
                    .gridLayout(adminEmployeeLayout)
                    .colName(empColNames[i])
                    .colType(empColTypes[i])
                    .headerName(empHeaders[i])
                    .sortOrder(i + 1);

            if ("position".equals(empColNames[i])) {
                colBuilder.enumValues("|주임|사원|대리|과장|차장|부장")
                          .enumKeys("|j|s|d|g|c|b");
            }
            if ("lastPasswordChangedAt".equals(empColNames[i]) || "lastLoginAt".equals(empColNames[i])) {
                colBuilder.format("yyyy-MM-dd HH:mm:ss");
            }
            if ("roleCodes".equals(empColNames[i])) {
                colBuilder.canEdit(0);
            }

            adminEmployeeLayout.getColumns().add(colBuilder.build());
        }
        gridLayoutRepository.save(adminEmployeeLayout);

        // ── adminMenuGrid ─────────────────────────────────────────────────────
        GridLayout adminMenuLayout = GridLayout.builder()
                .gridId("adminMenuGrid")
                .description("메뉴 관리 그리드")
                .mainCol("code")
                .deleting(0).selecting(0).editing(0).sorting(0)
                .focusWholeRow(1)
                .maxHeight(1).constHeight(1)
                .maxWidth(1).constWidth(1)
                .toolbarVisible(0)
                .build();

        adminMenuLayout.getColumns().add(GridLayoutColumn.builder()
                .gridLayout(adminMenuLayout).colName("code").colType("Text").headerName("코드").sortOrder(1).build());
        adminMenuLayout.getColumns().add(GridLayoutColumn.builder()
                .gridLayout(adminMenuLayout).colName("name").colType("Text").headerName("이름").sortOrder(2).build());
        gridLayoutRepository.save(adminMenuLayout);

        // ── adminRoleGrid ─────────────────────────────────────────────────────
        GridLayout adminRoleLayout = GridLayout.builder()
                .gridId("adminRoleGrid")
                .description("역할 관리 그리드")
                .deleting(0).selecting(0).editing(0)
                .focusWholeRow(1)
                .maxHeight(1).constHeight(1)
                .maxWidth(1).constWidth(1)
                .toolbarVisible(0)
                .build();

        adminRoleLayout.getColumns().add(GridLayoutColumn.builder()
                .gridLayout(adminRoleLayout).colName("code").colType("Text").headerName("역할 코드").width(120).sortOrder(1).build());
        adminRoleLayout.getColumns().add(GridLayoutColumn.builder()
                .gridLayout(adminRoleLayout).colName("description").colType("Text").headerName("설명").width(180).sortOrder(2).build());
        gridLayoutRepository.save(adminRoleLayout);

        // ── SampleGrid (employeeLayout) ───────────────────────────────────────
        GridLayout sampleLayout = GridLayout.builder()
                .gridId("SampleGrid")
                .description("샘플 그리드 (일반 직원 화면)")
                .mainCol("Name")
                .maxHeight(1).constHeight(1)
                .maxWidth(1).constWidth(1)
                .build();

        sampleLayout.getColumns().add(GridLayoutColumn.builder()
                .gridLayout(sampleLayout).colName("Name").colType("Text").headerName("항목 이름").width(200).canEdit(0).sortOrder(1).build());
        sampleLayout.getColumns().add(GridLayoutColumn.builder()
                .gridLayout(sampleLayout).colName("Value").colType("Int").headerName("수량").width(100).sortOrder(2).build());
        sampleLayout.getColumns().add(GridLayoutColumn.builder()
                .gridLayout(sampleLayout).colName("Date").colType("Date").headerName("날짜").width(120).format("yyyy-MM-dd").sortOrder(3).build());
        gridLayoutRepository.save(sampleLayout);

        log.info("Grid layouts initialized: adminEmployeeGrid, adminMenuGrid, adminRoleGrid, SampleGrid");
    }
}
