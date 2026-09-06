package com.ywy.rag.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 知识库服务接口：文件分片上传/列表/删除。实现见 {@code service.impl.KnowledgeServiceImpl}。
 */
public interface KnowledgeService {

    Long upload(Long userId, MultipartFile file, String visibility);

    List<com.ywy.rag.domain.KnowledgeDoc> list(Long userId);

    void delete(Long userId, Long docId);
}