package com.doosan.dframe.core.admin.employee;

import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminEmployeeController {

    @GetMapping("/employees")
    public String list(Model model) {
        return "admin/employees/list";
    }
}
