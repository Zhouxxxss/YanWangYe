package com.yanyan.module.rag.service;

import com.yanyan.module.rag.dto.AiAnswer;

/**
 * AI Provider 抽象（规划里"可切换模型商"的落点）。
 * 一期提供 Mock 实现；接入真实大模型 / 向量库时增加实现类并切换配置 yanyan.ai.provider。
 */
public interface AiProvider {

    /** 纯问答（无检索），供错因讲解、背诵抽背等内部场景。 */
    String chat(String systemPrompt, String userMessage);

    /** RAG 溯源问答：基于检索段落作答，带引用。 */
    AiAnswer ask(String question, java.util.List<String> retrievedContext);

    /** 文本向量化（接入向量库时用于入库 Embedding）。 */
    java.util.List<Float> embed(String text);
}