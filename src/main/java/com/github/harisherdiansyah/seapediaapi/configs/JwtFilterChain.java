package com.github.harisherdiansyah.seapediaapi.configs;

import com.github.harisherdiansyah.seapediaapi.core.utils.JwtUtility;
import com.github.harisherdiansyah.seapediaapi.core.utils.SecurityConstant;
import com.github.harisherdiansyah.seapediaapi.features.session.ActiveRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilterChain extends OncePerRequestFilter {
    private final JwtUtility jwtUtility;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (HttpMethod.GET.matches(method)) {
            for (String endpoint : SecurityConstant.PUBLIC_GET_ENDPOINTS) {
                if (antPathMatcher.match(endpoint, path) || antPathMatcher.match(endpoint.replace("/**", ""), path)) {
                    return true;
                }
            }
        } else if (HttpMethod.POST.matches(method)) {
            for (String endpoint : SecurityConstant.PUBLIC_POST_ENDPOINTS) {
                if (antPathMatcher.match(endpoint, path) || antPathMatcher.match(endpoint.replace("/**", ""), path)) {
                    return true;
                }
            }
        } else if (HttpMethod.PATCH.matches(method)) {
            for (String endpoint : SecurityConstant.PUBLIC_PATCH_ENDPOINTS) {
                if (antPathMatcher.match(endpoint, path) || antPathMatcher.match(endpoint.replace("/**", ""), path)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            try {
                final String at = authHeader.substring(7);
                String userRole = jwtUtility.extractRoles(at);

                ActiveRole activeRole = ActiveRole.valueOf(String.valueOf(userRole));
                List<GrantedAuthority> authorities = activeRole.getAuthorities();

                Authentication authToken = jwtUtility.buildAuthToken(at, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
