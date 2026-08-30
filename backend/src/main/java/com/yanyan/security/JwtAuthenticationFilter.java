package com.yanyan.security;

import com.yanyan.common.result.R;
import com.yanyan.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * JWT 认证过滤器：
 * 1) 解析并校验 token；
 * 2) 到 Redis 校验会话有效性（支持主动下线/黑名单）—— serve Redis 单点会话管控职责。
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final StringRedisTemplate stringRedisTemplate;
    /** Redis key 前缀，由 SecurityConfig 注入，避免解析期 @Value 失效 */
    private final String keyPrefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            Claims claims = jwtUtils.parse(token);
            if (claims != null) {
                Long userId = jwtUtils.getUserId(claims);
                // Redis 会话二次校验：若已下线/被踢，则拒绝
                String sessionKey = keyPrefix + "login:token:" + userId;
                if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(sessionKey))) {
                    String role = claims.get("role", String.class);
                    LoginUser.set(new LoginUser(userId, claims.get("username", String.class), role));
                    var auth = new UsernamePasswordAuthenticationToken(
                            LoginUser.get(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + (role == null ? "USER" : role))));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }

        try {
            chain.doFilter(request, response);
        } finally {
            LoginUser.clear();
        }
    }

    // 便捷写 JSON（全局异常/认证失败时前端仍能拿到统一结构）
    protected static void writeJson(HttpServletResponse response, R<?> body) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(body.getMessage() == null
                ? "{\"code\":" + body.getCode() + ",\"message\":\"error\"}"
                : "{\"code\":" + body.getCode() + ",\"message\":\"" + body.getMessage() + "\"}");
    }
}