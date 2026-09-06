package com.ywy.rag.service.impl;

import com.ywy.rag.ai.Embedder;
import com.ywy.rag.ai.LlmClient;
import com.ywy.rag.domain.KnowledgeDoc;
import com.ywy.rag.dto.AiAnswer;
import com.ywy.rag.dto.AskRequest;
import com.ywy.rag.mapper.KnowledgeDocMapper;
import com.ywy.rag.service.RagService;
import com.ywy.rag.vector.VectorStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Agentic RAG 答疑实现：
 * 提问 → 语义检索（按可见范围过滤）→ 上下文压缩 → LLM 生成 → 引用溯源。
 */
@Service
@RequiredArgsConstructor
public class RagServiceImpl implements RagService {

    private static final String COLLECTION = "ywy_knowledge";

    private final Embedder embedder;
    private final VectorStore vectorStore;
    private final LlmClient llm;
    private final KnowledgeDocMapper docMapper;

    @Override
    public AiAnswer ask(Long userId, AskRequest req) {
        List<VectorStore.Hit> hits = vectorStore.search(COLLECTION, embedder.embed(req.getQuestion()), req.getTopK() * 3);

        List<VectorStore.Hit> visible = hits.stream()
                .filter(h -> isVisible(userId, h.metadata()))
                .limit(req.getTopK() == null ? 5 : req.getTopK())
                .toList();

        String context = visible.stream()
                .map(h -> "【" + h.metadata().getOrDefault("title", "片段") + "】" + h.text())
                .collect(Collectors.joining("\n"));

        String systemPrompt = "你是「研王爷」考研伴学答疑助手。仅基于提供的参考资料回答，不编造；"
                + "若资料无法回答则说明并建议补充知识库。回答给出关键结论与依据。";
        String answer = llm.answer(systemPrompt, req.getQuestion(), context);

        List<AiAnswer.Citation> citations = new ArrayList<>();
        var docIdSet = new java.util.LinkedHashSet<Long>();
        for (VectorStore.Hit h : visible) {
            Long docId = toLong(h.metadata().get("docId"));
            if (docId == null || docIdSet.contains(docId)) continue;
            docIdSet.add(docId);
            KnowledgeDoc doc = docMapper.selectById(docId);
            citations.add(new AiAnswer.Citation(
                    docId,
                    doc == null ? (h.metadata().getOrDefault("title", "知识库文档")) : doc.getTitle(),
                    h.text().length() > 120 ? h.text().substring(0, 120) + "…" : h.text(),
                    toInt(h.metadata().get("chunkIndex"))));
        }

        return new AiAnswer(answer, citations);
    }

    private boolean isVisible(Long userId, Map<String, String> meta) {
        String visibility = meta.getOrDefault("visibility", "private");
        if ("public".equals(visibility) || "org".equals(visibility)) return true;
        Long owner = toLong(meta.get("userId"));
        return owner != null && owner.equals(userId);
    }

    private Long toLong(String s) {
        try { return s == null ? null : Long.valueOf(s); } catch (NumberFormatException e) { return null; }
    }

    private Integer toInt(String s) {
        try { return s == null ? null : Integer.valueOf(s); } catch (NumberFormatException e) { return null; }
    }
}