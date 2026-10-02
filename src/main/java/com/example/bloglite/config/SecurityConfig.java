package com.example.bloglite.config;

import com.example.bloglite.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // REST 接口不使用浏览器表单 CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // 本项目不使用 Spring Security 默认登录页
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // JWT 是无状态认证，不创建服务端 Session
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(authorize -> authorize
                        // 注册、登录、健康检查和错误页允许匿名访问
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/health",
                                "/error"
                        ).permitAll()

                        // 允许 CORS 预检请求
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // 公开阅读接口允许匿名访问
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/articles/**",
                                "/api/v1/categories/**",
                                "/api/v1/tags/**",
                                "/api/v1/archives/**",
                                "/api/v1/talks/**",
                                "/api/v1/links/**",
                                "/api/v1/albums/**"
                        ).permitAll()

                        // 其他接口需要登录
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exception -> exception
                        // 没有登录或 token 无效
                        .authenticationEntryPoint((
                                request,
                                response,
                                authenticationException
                        ) -> {
                            response.setStatus(
                                    HttpServletResponse.SC_UNAUTHORIZED
                            );
                            response.setContentType(
                                    MediaType.APPLICATION_JSON_VALUE
                            );
                            response.setCharacterEncoding(
                                    StandardCharsets.UTF_8.name()
                            );
                            response.getWriter().write(
                                    "{\"code\":40100," +
                                            "\"message\":\"未登录或登录状态已过期\"," +
                                            "\"data\":null}"
                            );
                        })

                        // 已登录但无权访问
                        .accessDeniedHandler((
                                request,
                                response,
                                accessDeniedException
                        ) -> {
                            response.setStatus(
                                    HttpServletResponse.SC_FORBIDDEN
                            );
                            response.setContentType(
                                    MediaType.APPLICATION_JSON_VALUE
                            );
                            response.setCharacterEncoding(
                                    StandardCharsets.UTF_8.name()
                            );
                            response.getWriter().write(
                                    "{\"code\":40300," +
                                            "\"message\":\"无权访问\"," +
                                            "\"data\":null}"
                            );
                        })
                )

                // JWT 过滤器放在用户名密码认证过滤器之前
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}