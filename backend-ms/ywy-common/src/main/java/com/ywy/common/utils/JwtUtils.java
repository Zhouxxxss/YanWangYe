package com.ywy.common.utils;

import com.ywy.common.autoconfigure.jwt.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具：签发访问/刷新令牌、解析校验。网关与 auth 模块共用。
 */
public class JwtUtils {

    private final SecretKey key;
    private final JwtProperties props;

    public JwtUtils(JwtProperties props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /** 签发访问令牌 */
    public String createToken(Long userId, String username, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("uid", userId)
                .claim("username", username)
                .claim("role", role == null ? "USER" : role)
                .issuedAt(new Date(now))
                .expiration(new Date(now + props.getExpireMs()))
                .signWith(key)
                .compact();
    }

    /** 签发刷新令牌（仅承载用户 id） */
    public String createRefreshToken(Long userId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("uid", userId)
                .issuedAt(new Date(now))
                .expiration(new Date(now + props.getRefreshExpireMs()))
                .signWith(key)
                .compact();
    }

    /** 解析校验；失败返回 null */
    public Claims parse(String token) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public Long getUserId(Claims claims) {
        Object uid = claims.get("uid");
        if (uid instanceof Number n) return n.longValue();
        return Long.valueOf(claims.getSubject());
    }

    public long getExpireMs() { return props.getExpireMs(); }
    public long getRefreshExpireMs() { return props.getRefreshExpireMs(); }
    public String getHeader() { return props.getHeader(); }
    public String getPrefix() { return props.getPrefix(); }
}