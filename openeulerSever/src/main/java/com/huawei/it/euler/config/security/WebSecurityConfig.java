package com.huawei.it.euler.config.security;

import com.huawei.it.euler.config.handler.EulerAccessDeniedHandler;
import com.huawei.it.euler.config.handler.EulerAuthenticationEntryPoint;
import com.huawei.it.euler.config.handler.EulerLogoutSuccessHandler;
import com.huawei.it.euler.filter.CsrfFilter;
import com.huawei.it.euler.filter.JwtTokenFilter;
import com.huawei.it.euler.filter.ProtocolFilter;
import com.huawei.it.euler.filter.SecurityFilter;
import com.huawei.it.euler.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Value("${url.whitelist}")
    private String urlWhitelist;

    @Resource
    private UserService userService;

    @Autowired
    private EulerAccessDeniedHandler eulerAccessDeniedHandler;

    @Autowired
    private EulerAuthenticationEntryPoint eulerAuthenticationEntryPoint;

    @Autowired
    private EulerLogoutSuccessHandler eulerLogoutSuccessHandler;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private CsrfFilter csrfFilter;

    @Autowired
    private ProtocolFilter protocolFilter;

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        return authProvider;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(urlWhitelist.split(","));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout.logoutSuccessHandler(eulerLogoutSuccessHandler))
                .authorizeHttpRequests(
                        authz -> authz.requestMatchers(urlWhitelist.split(","))
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .exceptionHandling(exceptions ->
                        exceptions.accessDeniedHandler(eulerAccessDeniedHandler)
                        .authenticationEntryPoint(eulerAuthenticationEntryPoint))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(csrfFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(protocolFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}