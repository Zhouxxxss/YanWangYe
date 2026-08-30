package com.yanyan.module.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI 溯源答疑结果。引用段落保留 docId + 段落位置，前端可点击跳转原文。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiAnswer {

    private String answer;

    /** 引用溯源列表 */
    private List<Citation> citations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Citation {
        private Long docId;
        private String docTitle;
        private String excerpt;
        /** 段落位置（页/序），前端跳转 anchor */
        private Integer chunkIndex;
    }
}