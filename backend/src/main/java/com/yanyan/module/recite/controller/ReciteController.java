package com.yanyan.module.recite.controller;

import com.yanyan.common.result.R;
import com.yanyan.module.recite.entity.ReciteCard;
import com.yanyan.module.recite.service.ReciteService;
import com.yanyan.security.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recite")
@RequiredArgsConstructor
public class ReciteController {

    private final ReciteService service;

    @GetMapping("/page")
    public R<?> page(@RequestParam(defaultValue = "1") int page,
                     @RequestParam(defaultValue = "20") int size,
                     @RequestParam(required = false) String subject) {
        return R.page(service.page(LoginUser.uid(), page, size, subject));
    }

    @PostMapping
    public R<Long> add(@RequestBody ReciteCard card) {
        return R.ok(service.add(LoginUser.uid(), card));
    }

    @PostMapping("/{id}/recite")
    public R<Void> recite(@PathVariable Long id, @RequestParam int quality) {
        service.recite(LoginUser.uid(), id, quality);
        return R.ok();
    }
}