package com.ywy.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 当前登录用户上下文。微服务模式下：
 * 网关解析 JWT 后把用户信息写入透传 Header，资源服务通过 {@code UserContextInterceptor} 读入本类。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContext implements Serializable {

    /** 网关 → 资源服务透传用户信息的 Header 名 */
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USERNAME = "X-Username";
    public static final String HEADER_ROLE = "X-User-Role";

    private Long userId;
    private String username;
    private String role;

    private static final ThreadLocal<UserContext> HOLDER = new ThreadLocal<>();

    public static void set(UserContext u) { HOLDER.set(u); }

    public static UserContext get() { return HOLDER.get(); }

    /** 当前登录用户 id；未登录返回 null */
    public static Long uid() {
        UserContext u = HOLDER.get();
        return u == null ? null : u.getUserId();
    }

    public static void clear() { HOLDER.remove(); }
}