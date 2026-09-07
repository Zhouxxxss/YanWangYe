package com.ywy.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywy.common.result.R;
import com.ywy.common.utils.JwtUtils;
import com.ywy.common.utils.UserContext;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 全局 JWT 鉴权过滤器：
 * 白名单放行；其余请求校验 Bearer Token，
 * 通过后剥离原始令牌并把用户身份写入转发 Header，供下游资源服务使用。
 * 配置了 ywy.jwt.secret 才注入（见 ywy-common 的 JwtAutoConfiguration）。
 */
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    private final AntPathMatcher matcher = new AntPathMatcher();

    private static final String[] WHITELIST = {
            "/auth/login", "/auth/register", "/auth/refresh", "/auth/wx/login",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/webjars/**", "/doc.html",
            "/actuator/**", "/favicon.ico",
    };
    // 注：logout / me 不在白名单，需携带令牌（会话下线与用户信息依赖网关透传身份）

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        if (isWhitelist(path)) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest());
        Claims claims = token != null ? jwtUtils.parse(token) : null;
        if (claims == null || jwtUtils.getUserId(claims) == null) {
            return unauthorized(exchange, R.unauthorized("未登录或登录已过期"));
        }

        // 剥离原始令牌，透传用户身份
        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .headers(h -> {
                    h.remove("Authorization");
                    h.set(UserContext.HEADER_USER_ID, String.valueOf(jwtUtils.getUserId(claims)));
                    String username = claims.get("username", String.class);
                    if (username != null) {
                        h.set(UserContext.HEADER_USERNAME, username);
                    }
                    String role = claims.get("role", String.class);
                    if (role != null) {
                        h.set(UserContext.HEADER_ROLE, role);
                    }
                })
                .build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private String extractToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private boolean isWhitelist(String path) {
        for (String p : WHITELIST) {
            if (matcher.match(p, path)) return true;
        }
        return false;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, R<?> body) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] bytes = toJson(body).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            return "{\"code\":401,\"message\":\"unauthorized\"}";
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}