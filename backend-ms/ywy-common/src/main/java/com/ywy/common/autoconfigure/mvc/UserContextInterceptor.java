package com.ywy.common.autoconfigure.mvc;

import com.ywy.common.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 资源服务用户上下文注入拦截器：
 * 读取网关透传的用户 Header，写入 {@link UserContext}，请求结束清理。
 */
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        String uid = request.getHeader(UserContext.HEADER_USER_ID);
        if (StringUtils.hasText(uid)) {
            UserContext ctx = new UserContext(
                    Long.valueOf(uid),
                    request.getHeader(UserContext.HEADER_USERNAME),
                    request.getHeader(UserContext.HEADER_ROLE));
            UserContext.set(ctx);
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        UserContext.clear();
    }
}