package com.ywy.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * RAG 溯源答疑结果。引用段落保留 docId + 分片位置，前端可点击跳转原文。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAnswer {

    private String answer;

    private List<Citation> citations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Citation {
        private Long docId;
        private String docTitle;
        private String excerpt;
        private Integer chunkIndex;
    }
}