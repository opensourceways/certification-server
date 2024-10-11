package com.huawei.it.euler.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.huawei.it.euler.config.handler.EulerAccessDeniedHandler;
import com.huawei.it.euler.config.handler.EulerAuthenticationEntryPoint;
import com.huawei.it.euler.config.handler.EulerLogoutSuccessHandler;
import com.huawei.it.euler.ddd.domain.account.UserInfoService;
import com.huawei.it.euler.filter.CsrfFilter;
import com.huawei.it.euler.filter.JwtTokenFilter;
import com.huawei.it.euler.filter.SecurityFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Value("${url.whitelist}")
    private String urlWhitelist;

    @Autowired
    private UserInfoService userInfoService;

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
    private SecurityFilter securityFilter;

    @Bean
    public TimeoutCallableProcessingInterceptor timeoutInterceptor() {
        return new TimeoutCallableProcessingInterceptor();
    }

    @Configuration
    public class AsyncConfig implements WebMvcConfigurer {
        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            configurer.setDefaultTimeout(120000); // 设置异步请求超时时间为30秒
            configurer.registerCallableInterceptors(timeoutInterceptor());
        }
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userInfoService);
        return authProvider;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(urlWhitelist.split(","));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .csrf(csrf -> csrf.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
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
                .headers(headers -> headers.defaultsDisabled().cacheControl(Customizer.withDefaults()))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(csrfFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}