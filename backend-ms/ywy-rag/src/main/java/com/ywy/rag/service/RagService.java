package com.ywy.rag.service;

import com.ywy.rag.dto.AiAnswer;
import com.ywy.rag.dto.AskRequest;

/**
 * Agentic RAG 答疑服务接口。实现见 {@code service.impl.RagServiceImpl}。
 */
public interface RagService {

    AiAnswer ask(Long userId, AskRequest req);
}