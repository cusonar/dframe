package com.doosan.dframe.approval;

import com.doosan.dframe.admin.employee.Employee;
import com.doosan.dframe.admin.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/approval")
@RequiredArgsConstructor
public class ApprovalController {

    private final ApprovalService approvalService;
    private final ApprovalDocumentRepository documentRepository;
    private final ApprovalLineRepository lineRepository;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/draft")
    public String draftForm(Model model) {
        model.addAttribute("title", "기안 작성");
        model.addAttribute("employees", employeeRepository.findAll());
        return "approval/draft";
    }

    @PostMapping("/draft")
    public String submitDraft(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam List<Long> approverIds,
            Authentication authentication) {
        
        String username = authentication.getName();
        Employee drafter = employeeRepository.findByUsername(username).orElseThrow();
        
        approvalService.draftDocument(drafter.getId(), title, content, approverIds);
        
        return "redirect:/approval/pending";
    }

    @GetMapping("/pending")
    public String pendingList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model,
            Authentication authentication) {
        
        String username = authentication.getName();
        Employee me = employeeRepository.findByUsername(username).orElseThrow();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ApprovalLine> pendingLines = lineRepository.findByApproverAndStatus(me, ApprovalLineStatus.PENDING, pageable);
        
        model.addAttribute("title", "결재 대기함");
        model.addAttribute("pendingLines", pendingLines.getContent());
        model.addAttribute("page", pendingLines);
        return "approval/pending";
    }

    @PostMapping("/process/{lineId}")
    public String processApproval(
            @PathVariable Long lineId,
            @RequestParam String action, // "approve" or "reject"
            @RequestParam(required = false) String comment) {
        
        boolean isApproved = "approve".equals(action);
        approvalService.processApproval(lineId, isApproved, comment);
        
        return "redirect:/approval/pending";
    }
}
