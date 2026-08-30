package com.yanyan.module.rag.service;

import com.yanyan.common.exception.BizException;
import com.yanyan.module.rag.dto.AiAnswer;
import com.yanyan.module.rag.dto.AskRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * RAG 溯源答疑服务。一期流程：
 * 用户提问 → 按可见范围权限过滤 → (向量召回，待入库真实实现) → LLM 生成 → 引用溯源。
 * 权限过滤：同一文档分 公共/私人/组织 可见，召回阶段按用户可见范围过滤，避免越权。
 */
@Service
@RequiredArgsConstructor
public class RagService {

    private final AiProvider aiProvider;

    public AiAnswer ask(Long userId, AskRequest req) {
        // 1. 检索可见文档段落（权限过滤在此进行）
        //    TODO: 接入文档系统 + 向量库后填充：doc_permission 校验 + vector_index 召回
        java.util.List<String> context = RAG_STUB_RETRIEVAL(userId, req.getScope());
        // 2. 带上下文生成 + 溯源
        return aiProvider.ask(req.getQuestion(), context);
    }

    /** 错题讲解 / 背诵抽背等内部问答的通用入口。 */
    public String chat(String systemPrompt, String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            throw new BizException("内容不能为空");
        }
        return aiProvider.chat(systemPrompt, userMessage);
    }

    /** 占位检索：一期返回空上下文。二期接入向量库后删除。 */
    private java.util.List<String> RAG_STUB_RETRIEVAL(Long userId, String scope) {
        return java.util.List.of();
    }
}