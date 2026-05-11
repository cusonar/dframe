package com.doosan.dframe.admin.employee;

import com.doosan.dframe.admin.department.DepartmentRepository;
import com.doosan.dframe.admin.role.RoleRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class AdminEmployeeApiController {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final EmployeeService employeeService;

    @GetMapping
    public Map<?, ?> list(Model model) {
        Gson gson = new Gson();
        String json = "{\n" + //
                "  \"Body\": [\n" + //
                "    [\n" + //
                "      { \"id\": \"1\", \"Name\": \"프로젝트 A\", \"Value\": \"100\", \"Date\": \"2024-05-11\",\n" + //
                "        \"Items\": [\n" + //
                "          { \"id\": \"1-1\", \"Name\": \"태스크 1\", \"Value\": \"50\", \"Date\": \"2024-05-12\" },\n" + //
                "          { \"id\": \"1-2\", \"Name\": \"태스크 2\", \"Value\": \"50\", \"Date\": \"2024-05-13\" }\n" + //
                "        ]\n" + //
                "      },\n" + //
                "      { \"id\": \"2\", \"Name\": \"프로젝트 B\", \"Value\": \"200\", \"Date\": \"2024-06-01\" }\n" + //
                "    ]\n" + //
                "  ]\n" + //
                "}";
        return gson.fromJson(json, Map.class);
    }

}
