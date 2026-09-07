package com.ywy.community.placeholder;

import com.ywy.common.result.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 二期占位：发帖 / 好友聊天。无需实现，仅保证 /community/** 可路由。
 */
@RestController
@RequestMapping("/community")
public class CommunityPlaceholderController {

    @GetMapping
    public R<Map<String, Object>> info() {
        return R.ok(Map.of("module", "community", "stage", "二期预留",
                "todo", "发帖 / 好友聊天（二期上线）"));
    }
}