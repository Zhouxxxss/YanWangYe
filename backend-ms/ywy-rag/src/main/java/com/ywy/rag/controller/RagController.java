package com.ywy.rag.controller;

import com.ywy.common.result.R;
import com.ywy.common.utils.UserContext;
import com.ywy.rag.dto.AiAnswer;
import com.ywy.rag.dto.AskRequest;
import com.ywy.rag.service.RagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    @PostMapping("/ask")
    public R<AiAnswer> ask(@Valid @RequestBody AskRequest req) {
        return R.ok(ragService.ask(UserContext.uid(), req));
    }
}