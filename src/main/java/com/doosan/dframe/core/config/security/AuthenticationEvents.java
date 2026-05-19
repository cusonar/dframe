package com.doosan.dframe.core.config.security;

import com.doosan.dframe.core.employee.Employee;
import com.doosan.dframe.core.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AuthenticationEvents {

    private final EmployeeRepository employeeRepository;

    @EventListener
    @Transactional
    public void onSuccess(AuthenticationSuccessEvent event) {
        if (event.getAuthentication().getPrincipal() instanceof Employee employee) {
            Employee savedEmployee = employeeRepository.findById(employee.getId()).orElse(null);
            if (savedEmployee != null) {
                savedEmployee.resetLoginFail();
                savedEmployee.updateLastLogin();
            }
        }
    }

    @EventListener
    @Transactional
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        Employee employee = employeeRepository.findById(username).orElse(null);
        if (employee != null) {
            employee.incrementLoginFail();
            if (employee.getCountLoginFail() >= 5) {
                employee.updateAuthStatus(null, null, false, null); // 계정 잠금
            }
        }
    }
}
