package com.yanyan.module.wrongbook.controller;

import com.yanyan.common.result.R;
import com.yanyan.module.wrongbook.entity.WrongQuestion;
import com.yanyan.module.wrongbook.service.WrongQuestionService;
import com.yanyan.security.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wrongbook")
@RequiredArgsConstructor
public class WrongQuestionController {

    private final WrongQuestionService service;

    @GetMapping("/page")
    public R<?> page(@RequestParam(defaultValue = "1") int page,
                     @RequestParam(defaultValue = "10") int size,
                     @RequestParam(required = false) String subject) {
        return R.page(service.page(LoginUser.uid(), page, size, subject));
    }

    @GetMapping("/{id}")
    public R<WrongQuestion> get(@PathVariable Long id) {
        return R.ok(service.get(LoginUser.uid(), id));
    }

    @PostMapping
    public R<Long> add(@Valid @RequestBody WrongQuestion wq) {
        return R.ok(service.add(LoginUser.uid(), wq));
    }

    @PostMapping("/{id}/review")
    public R<Void> review(@PathVariable Long id, @RequestParam int quality) {
        service.review(LoginUser.uid(), id, quality);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        service.delete(LoginUser.uid(), id);
        return R.ok();
    }
}