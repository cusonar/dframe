package com.doosan.dframe.core.config.security;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JdbcTemplate jdbcTemplate;
    private final UserDetailsService userDetailsService;
    private final CustomOidcUserService customOidcUserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient;

    public SecurityConfig(JdbcTemplate jdbcTemplate, UserDetailsService userDetailsService,
                          CustomOidcUserService customOidcUserService,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                          OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDetailsService = userDetailsService;
        this.customOidcUserService = customOidcUserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.accessTokenResponseClient = accessTokenResponseClient;
    }

    @Bean
    public PersistentTokenRepository tokenRepository() {
        return new JdbcPersistentTokenRepository(jdbcTemplate);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/adminlte/**", "/login", "/error")
                        .permitAll()
                        .requestMatchers("/h2-console/**")
                        .permitAll()
                        .requestMatchers("/login/oauth2/code/**", "/oauth2/**")
                        .permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login")
                        .loginProcessingUrl("/login_proc")
                        .defaultSuccessUrl("/").permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .tokenEndpoint(token -> token
                                .accessTokenResponseClient(accessTokenResponseClient))
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oauth2FailureHandler()))
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true))
                .rememberMe(remember -> remember
                        .key("dframe-remember-me-key")
                        .tokenRepository(tokenRepository())
                        .userDetailsService(userDetailsService)
                        .tokenValiditySeconds(86400 * 30) // 30 days
                        .rememberMeParameter("remember-me"))
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations()).requestMatchers("/.well-known/appspecific/com.chrome.devtools.json");
    }

    private SimpleUrlAuthenticationFailureHandler oauth2FailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler() {
            {
                setDefaultFailureUrl("/login?error");
                setUseForward(false);
            }

            @Override
            public void onAuthenticationFailure(jakarta.servlet.http.HttpServletRequest request,
                                                jakarta.servlet.http.HttpServletResponse response,
                                                org.springframework.security.core.AuthenticationException exception)
                    throws java.io.IOException, jakarta.servlet.ServletException {
                // 에러 메시지를 세션에 저장
                request.getSession().setAttribute("OAUTH2_LOGIN_ERROR", exception.getMessage());
                super.onAuthenticationFailure(request, response, exception);
            }
        };
    }
}
