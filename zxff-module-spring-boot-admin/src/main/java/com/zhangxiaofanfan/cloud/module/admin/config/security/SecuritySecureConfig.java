package com.zhangxiaofanfan.cloud.module.admin.config.security;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.UUID;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;


@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class SecuritySecureConfig {

    private final AdminServerProperties adminServer;

    private final SecurityProperties security;

    @Value(value = "${management.endpoints.web.base-path:}")
    private String actuatorPrefix;

    public SecuritySecureConfig(AdminServerProperties adminServer, SecurityProperties security) {
        this.adminServer = adminServer;
        this.security = security;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setTargetUrlParameter("redirectTo");
        handler.setDefaultTargetUrl(this.adminServer.path("/"));

        http
                .authorizeHttpRequests(
                        (authorizeRequests) -> authorizeRequests
                                // admin 登录完成跳转之后需要根目录的 css 和 js 文件, 跳过验证
                                .requestMatchers(new AntPathRequestMatcher(this.adminServer.path("/*.js"))).permitAll()
                                .requestMatchers(new AntPathRequestMatcher(this.adminServer.path("/*.css"))).permitAll()
                                .requestMatchers(
                                        new AntPathRequestMatcher(this.adminServer.path("/assets/**"))
                                ).permitAll()
                                .requestMatchers(
                                        new AntPathRequestMatcher(this.adminServer.path(actuatorPrefix + "/**"))
                                ).permitAll()
                                .requestMatchers(
                                        new AntPathRequestMatcher(this.adminServer.path(actuatorPrefix + "/**"))
                                ).permitAll()
                                .requestMatchers(new AntPathRequestMatcher(this.adminServer.path("/login"))).permitAll()
                                .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin((formLogin) -> formLogin.loginPage(this.adminServer.path("/login"))
                        .successHandler(handler)
                        .failureHandler((request, response, exception) -> {})
                )
                .logout((logout) -> logout.logoutUrl(this.adminServer.path("/logout")))
                .httpBasic(Customizer.withDefaults())
                // 使用 Cookies启用 CSRF-Protection
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                        .ignoringRequestMatchers(
                                // (取消)注册的端点禁用 CSRF-Protection
                                new AntPathRequestMatcher(this.adminServer.path("/instances"), POST.toString()),
                                new AntPathRequestMatcher(this.adminServer.path("/instances/*"), DELETE.toString()),
                                // 禁用 Actuator 端点的 CSRF-Protection 功能。
                                new AntPathRequestMatcher(this.adminServer.path(actuatorPrefix + "/**"))
                        )
                )
                .rememberMe((rememberMe) -> rememberMe.key(UUID.randomUUID().toString()).tokenValiditySeconds(1209600));

        return http.build();

    }

    // Required to provide UserDetailsService for "remember functionality"
    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User
                .withUsername(security.getUser().getName())
                .password(passwordEncoder.encode(security.getUser().getPassword()))
                .roles("USER").build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}