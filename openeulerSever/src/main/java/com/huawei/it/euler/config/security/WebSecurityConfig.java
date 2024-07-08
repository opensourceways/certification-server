/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.it.euler.config.security;

import com.huawei.it.euler.config.handler.EulerAccessDeniedHandler;
import com.huawei.it.euler.config.handler.EulerAuthenticationEntryPoint;
import com.huawei.it.euler.config.handler.EulerLogoutSuccessHandler;
import com.huawei.it.euler.filter.CsrfFilter;
import com.huawei.it.euler.filter.JwtTokenFilter;
import com.huawei.it.euler.filter.ProtocolFilter;
import com.huawei.it.euler.filter.SecurityFilter;
import com.huawei.it.euler.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import javax.annotation.Resource;

/**
 * WebSecurity配置
 *
 * @since 2024/06/28
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf((csrf) -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .logout()
                .logoutSuccessHandler(eulerLogoutSuccessHandler)
                .and()
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(eulerAccessDeniedHandler)
                .authenticationEntryPoint(eulerAuthenticationEntryPoint)
                .and()
                .addFilter(securityFilter())
                .addFilter(csrfFilter())
                .addFilter(jwtTokenFilter())
                .addFilter(protocolFilter());
    }

    @Bean
    public JwtTokenFilter jwtTokenFilter() throws Exception {
        return new JwtTokenFilter(authenticationManager());
    }

    @Bean
    public CsrfFilter csrfFilter() throws Exception {
        return new CsrfFilter(authenticationManager());
    }

    @Bean
    public ProtocolFilter protocolFilter() throws Exception {
        return new ProtocolFilter(authenticationManager());
    }

    @Bean
    public SecurityFilter securityFilter() throws Exception {
        return new SecurityFilter(authenticationManager());
    }
}
