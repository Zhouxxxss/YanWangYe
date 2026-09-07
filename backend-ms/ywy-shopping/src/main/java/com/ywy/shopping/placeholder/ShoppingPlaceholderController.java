package com.ywy.shopping.placeholder;

import com.ywy.common.result.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 二期占位：二手资料 / 商家入驻。保证 /shopping/** 可路由。
 */
@RestController
@RequestMapping("/shopping")
public class ShoppingPlaceholderController {

    @GetMapping
    public R<Map<String, Object>> info() {
        return R.ok(Map.of("module", "shopping", "stage", "二期预留",
                "todo", "二手考研资料交易 / 商家入驻（二期上线）"));
    }
}