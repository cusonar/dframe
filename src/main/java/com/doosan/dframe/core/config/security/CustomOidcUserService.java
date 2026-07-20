package com.doosan.dframe.core.config.security;

import com.doosan.dframe.core.employee.Employee;
import com.doosan.dframe.core.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        // Azure AD에서 이메일 추출 (preferred_username 또는 email claim 사용)
        String email = oidcUser.getEmail();
        if (email == null) {
            email = oidcUser.getAttribute("preferred_username");
        }

        log.info("Azure AD 로그인 시도 - email: {}, claims: {}", email, oidcUser.getClaims().keySet());

        if (email == null) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_not_found"),
                    "Azure AD 응답에서 이메일을 찾을 수 없습니다."
            );
        }

        // 이메일로 기존 Employee 검색 (roles/authorities까지 fetch join)
        Employee employee = employeeRepository.findByEmailWithRoles(email).orElse(null);

        if (employee == null) {
            log.warn("Azure AD 로그인 실패 - 등록되지 않은 사용자: {}", email);
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("user_not_found"),
                    "등록되지 않은 사용자입니다: " + email
            );
        }

        // 계정 상태 검증
        if (!employee.isEnabled()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("account_disabled"),
                    "비활성화된 계정입니다."
            );
        }

        if (!employee.isAccountNonLocked()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("account_locked"),
                    "잠긴 계정입니다."
            );
        }

        // Employee의 권한을 OIDC 사용자에 부여
        Set<GrantedAuthority> authorities = new HashSet<>(employee.getAuthorities());
        authorities.addAll(oidcUser.getAuthorities());

        log.info("Azure AD 로그인 성공 - employee: {}, authorities: {}", employee.getId(), authorities);

        return new DefaultOidcUser(
                authorities,
                oidcUser.getIdToken(),
                oidcUser.getUserInfo()
        );
    }
}
