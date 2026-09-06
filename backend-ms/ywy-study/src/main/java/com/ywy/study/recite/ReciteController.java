package com.ywy.study.recite;

import com.ywy.common.result.R;
import com.ywy.common.utils.UserContext;
import com.ywy.study.common.PageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recite")
@RequiredArgsConstructor
public class ReciteController {

    private final ReciteService service;

    @GetMapping("/page")
    public R<PageVO<ReciteCard>> page(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "20") int size,
                                      @RequestParam(required = false) String subject) {
        return R.ok(service.page(UserContext.uid(), page, size, subject));
    }

    @PostMapping
    public R<Long> add(@RequestBody ReciteCard card) {
        return R.ok(service.add(UserContext.uid(), card));
    }

    @PostMapping("/{id}/recite")
    public R<Void> recite(@PathVariable Long id, @RequestParam int quality) {
        service.recite(UserContext.uid(), id, quality);
        return R.ok();
    }
}