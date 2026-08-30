package com.yanyan.module.rag.service.impl;

import com.yanyan.module.rag.dto.AiAnswer;
import com.yanyan.module.rag.service.AiProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Mock 实现：未接入真实大模型前保证系统可运行、前后端可联调。
 * 接入正式模型（配置 yanyan.ai.provider=openai/dashscope/...）后由真实实现替代。
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "yanyan.ai.provider", havingValue = "mock", matchIfMissing = true)
public class MockAiProvider implements AiProvider {

    @Override
    public String chat(String systemPrompt, String userMessage) {
        log.debug("[AI-MOCK] chat: {}", userMessage);
        return "【Mock 回答】" + userMessage + "。请接入真实大模型以获得准确答疑与溯源。";
    }

    @Override
    public AiAnswer ask(String question, List<String> retrievedContext) {
        log.debug("[AI-MOCK] ask: {}，召回段落 {} 条", question, retrievedContext.size());
        AiAnswer ans = new AiAnswer();
        ans.setAnswer("【Mock 答疑】基于召回资料作答：" + question);
        ans.setCitations(Collections.emptyList());
        return ans;
    }

    @Override
    public List<Float> embed(String text) {
        // 接入向量库前：返回空占位，避免 NPE
        return Collections.emptyList();
    }
}