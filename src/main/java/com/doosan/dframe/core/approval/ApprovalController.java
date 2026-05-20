package com.doosan.dframe.core.approval;

import ch.qos.logback.core.model.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/approvals")
@RequiredArgsConstructor
public class ApprovalController {

    @GetMapping
    public String list(Model model) {
        return "approvals/list";
    }

}
