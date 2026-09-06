package com.ywy.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 简易令牌桶限流过滤器（内存实现，按客户端 IP 限流）。
 * 生产可替换为 Redis + Lua 的分布式限流，此处作为一期演示/兜底。
 */
@Component
@RequiredArgsConstructor
public class RateLimitGlobalFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper;

    /** 每秒补充令牌数（约 600 req/min per IP） */
    private static final double TOKENS_PER_SEC = 10.0;
    private static final double BUCKET_CAPACITY = 20.0;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String ip = exchange.getRequest().getRemoteAddress() == null
                ? "unknown" : exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        if (!buckets.computeIfAbsent(ip, k -> new Bucket()).tryAcquire(1)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            byte[] bytes = toJson().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        }
        return chain.filter(exchange);
    }

    private String toJson() {
        try {
            return objectMapper.writeValueAsString(new Failure(429, "请求过于频繁，请稍后再试"));
        } catch (JsonProcessingException e) {
            return "{\"code\":429,\"message\":\"too many requests\"}";
        }
    }

    public record Failure(int code, String message) {}

    private static final class Bucket {
        private final AtomicLong tokens = new AtomicLong((long) BUCKET_CAPACITY);
        private volatile long lastRefill = System.currentTimeMillis();

        boolean tryAcquire(int n) {
            refill();
            long cur = tokens.get();
            while (cur >= n) {
                if (tokens.compareAndSet(cur, cur - n)) return true;
                cur = tokens.get();
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefill;
            lastRefill = now;
            if (elapsed <= 0) return;
            double gained = (elapsed / 1000.0) * TOKENS_PER_SEC;
            tokens.accumulateAndGet((long) gained, (a, b) ->
                    Math.min((long) BUCKET_CAPACITY, a + b));
        }
    }

    @Override
    public int getOrder() {
        return -90;
    }
}