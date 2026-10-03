package com.example.bloglite.security;

import com.example.bloglite.service.ResourceAuthorizationService;
import com.example.bloglite.service.ResourceAuthorizationService.AuthorizationDecision;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 动态接口资源授权过滤器
 */
@Component
public class ResourceAuthorizationFilter extends OncePerRequestFilter {

    private final ResourceAuthorizationService resourceAuthorizationService;

    public ResourceAuthorizationFilter(
            ResourceAuthorizationService resourceAuthorizationService
    ) {
        this.resourceAuthorizationService = resourceAuthorizationService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        boolean authenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        List<String> authorities = authentication == null
                ? List.of()
                : authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

        AuthorizationDecision decision =
                resourceAuthorizationService.authorize(
                        request.getMethod(),
                        request.getRequestURI(),
                        authenticated,
                        authorities
                );

        if (decision.allowed()) {
            filterChain.doFilter(request, response);
            return;
        }

        int status = decision.requiresAuthentication()
                ? HttpServletResponse.SC_UNAUTHORIZED
                : HttpServletResponse.SC_FORBIDDEN;
        int code = decision.requiresAuthentication() ? 40100 : 40300;

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                "{\"code\":" + code +
                        ",\"message\":\"" + decision.message() +
                        "\",\"data\":null}"
        );
    }
}
