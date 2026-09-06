package com.ywy.study.wrongbook;

import com.ywy.common.result.R;
import com.ywy.study.common.PageVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/wrongbook")
@RequiredArgsConstructor
public class WrongQuestionController {

    private final WrongQuestionService service;

    @GetMapping("/page")
    public R<PageVO<WrongQuestion>> page(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) String subject) {
        return R.ok(service.page(com.ywy.common.utils.UserContext.uid(), page, size, subject));
    }

    @GetMapping("/{id}")
    public R<WrongQuestion> get(@PathVariable Long id) {
        return R.ok(service.get(com.ywy.common.utils.UserContext.uid(), id));
    }

    @PostMapping
    public R<Long> add(@Valid @RequestBody WrongQuestion wq) {
        return R.ok(service.add(com.ywy.common.utils.UserContext.uid(), wq));
    }

    @PostMapping("/{id}/review")
    public R<Void> review(@PathVariable Long id, @RequestParam int quality) {
        service.review(com.ywy.common.utils.UserContext.uid(), id, quality);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        service.delete(com.ywy.common.utils.UserContext.uid(), id);
        return R.ok();
    }

    /** 遗忘曲线查看入口（错题复习计划图）。 */
    @GetMapping("/forget-curve")
    public R<Map<String, Object>> curve() {
        return R.ok(service.forgetCurve());
    }
}