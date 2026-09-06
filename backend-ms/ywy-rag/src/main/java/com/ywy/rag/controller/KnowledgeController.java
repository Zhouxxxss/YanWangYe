package com.ywy.rag.controller;

import com.ywy.common.result.R;
import com.ywy.common.utils.UserContext;
import com.ywy.rag.domain.KnowledgeDoc;
import com.ywy.rag.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    /** 文件分片与上传：一次接口完成 存储→切块→向量化。 */
    @PostMapping("/upload")
    public R<Long> upload(@RequestPart("file") MultipartFile file,
                          @RequestParam(defaultValue = "private") String visibility) {
        return R.ok(knowledgeService.upload(UserContext.uid(), file, visibility));
    }

    @GetMapping
    public R<List<KnowledgeDoc>> list() {
        return R.ok(knowledgeService.list(UserContext.uid()));
    }

    @DeleteMapping("/{docId}")
    public R<Void> delete(@PathVariable Long docId) {
        knowledgeService.delete(UserContext.uid(), docId);
        return R.ok();
    }
}