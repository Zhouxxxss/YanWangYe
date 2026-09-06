package com.ywy.rag.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ywy.common.exceptions.BizException;
import com.ywy.rag.ai.Embedder;
import com.ywy.rag.common.ChunkingService;
import com.ywy.rag.domain.KnowledgeChunk;
import com.ywy.rag.domain.KnowledgeDoc;
import com.ywy.rag.mapper.KnowledgeChunkMapper;
import com.ywy.rag.mapper.KnowledgeDocMapper;
import com.ywy.rag.service.KnowledgeService;
import com.ywy.rag.storage.ObjectStorage;
import com.ywy.rag.vector.VectorStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 知识库服务实现：上传 → 落到对象存储 → 切块 → 向量化入库（含分片表元数据）。
 */
@Service
@RequiredArgsConstructor
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeDocMapper docMapper;
    private final KnowledgeChunkMapper chunkMapper;
    private final ObjectStorage storage;
    private final ChunkingService chunking;
    private final Embedder embedder;
    private final VectorStore vectorStore;

    private static final String COLLECTION = "ywy_knowledge";

    @Override
    @Transactional
    public Long upload(Long userId, MultipartFile file, String visibility) {
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new BizException("文件名不能为空");
        }
        String ext = filename.contains(".")
                ? filename.substring(filename.lastIndexOf('.') + 1).toLowerCase() : "";

        String objectPath = userId + "/" + UUID.randomUUID().toString().replace("-", "") + "." + ext;
        try {
            storage.put(objectPath, file.getBytes(), file.getContentType());
        } catch (java.io.IOException e) {
            throw new BizException("文件读取失败");
        }

        KnowledgeDoc doc = new KnowledgeDoc();
        doc.setUserId(userId);
        doc.setTitle(filename);
        doc.setSourceType("manual");
        doc.setVisibility(visibility == null ? "private" : visibility);
        doc.setObjectPath(objectPath);
        doc.setStatus("PROCESSING");
        doc.setChunkCount(0);
        docMapper.insert(doc);

        String text = extractText(file, ext);
        List<ChunkingService.TextChunk> chunks = chunking.chunk(text);

        for (ChunkingService.TextChunk c : chunks) {
            KnowledgeChunk kc = new KnowledgeChunk();
            kc.setDocId(doc.getId());
            kc.setChunkIndex(c.index());
            kc.setContent(c.text());
            kc.setVectorId(doc.getId() + "_" + c.index());
            chunkMapper.insert(kc);
            vectorStore.upsert(COLLECTION, kc.getVectorId(),
                    embedder.embed(c.text()), c.text(), Map.of(
                            "docId", String.valueOf(doc.getId()),
                            "userId", String.valueOf(userId),
                            "title", doc.getTitle(),
                            "visibility", doc.getVisibility(),
                            "chunkIndex", String.valueOf(c.index())));
        }
        doc.setChunkCount(chunks.size());
        doc.setStatus(chunks.isEmpty() ? "FAILED" : "READY");
        docMapper.updateById(doc);
        return doc.getId();
    }

    private String extractText(MultipartFile file, String ext) {
        try {
            byte[] bytes = file.getBytes();
            if (List.of("txt", "md", "markdown", "java", "py", "sql", "json", "yml", "properties").contains(ext)) {
                return new String(bytes, StandardCharsets.UTF_8);
            }
            if ("pdf".equals(ext)) {
                // TODO: 接入 PDF 解析器（一期仅入库元数据）
                return "（PDF 文本抽取待接入，当前为空）";
            }
            return "";
        } catch (java.io.IOException e) {
            throw new BizException("文件读取失败");
        }
    }

    @Override
    public List<KnowledgeDoc> list(Long userId) {
        return docMapper.selectList(Wrappers.<KnowledgeDoc>lambdaQuery()
                .eq(KnowledgeDoc::getUserId, userId)
                .orderByDesc(KnowledgeDoc::getCreateTime));
    }

    @Override
    @Transactional
    public void delete(Long userId, Long docId) {
        KnowledgeDoc doc = docMapper.selectById(docId);
        if (doc == null || !doc.getUserId().equals(userId)) {
            throw new BizException("文档不存在或无权限");
        }
        List<KnowledgeChunk> chunks = chunkMapper.selectList(
                Wrappers.<KnowledgeChunk>lambdaQuery().eq(KnowledgeChunk::getDocId, docId));
        chunkMapper.delete(Wrappers.<KnowledgeChunk>lambdaQuery().eq(KnowledgeChunk::getDocId, docId));
        vectorStore.delete(COLLECTION, chunks.stream().map(KnowledgeChunk::getVectorId).toList());
        storage.delete(doc.getObjectPath());
        docMapper.deleteById(docId);
    }
}