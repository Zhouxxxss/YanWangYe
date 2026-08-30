package com.yanyan.module.rag.controller;

import com.yanyan.common.result.R;
import com.yanyan.module.rag.dto.AiAnswer;
import com.yanyan.module.rag.dto.AskRequest;
import com.yanyan.module.rag.service.RagService;
import com.yanyan.security.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
public class RagController {

    private final RagService ragService;

    @PostMapping("/ask")
    public R<AiAnswer> ask(@Valid @RequestBody AskRequest req) {
        return R.ok(ragService.ask(LoginUser.uid(), req));
    }
}