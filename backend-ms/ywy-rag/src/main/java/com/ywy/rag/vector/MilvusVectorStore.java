package com.ywy.rag.vector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Milvus 向量检索接入点（用户指定）。自动装配条件：{@code ywy.rag.vector.store=milvus}
 * 或 {@code ywy.rag.milvus.enabled=true}。接入 milvus-sdk-java 后补齐对应调用。
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "ywy.rag.vector", name = "store", havingValue = "milvus")
public class MilvusVectorStore implements VectorStore {

    public MilvusVectorStore() {
        log.info("[RAG] 启用 Milvus 向量检索接入点（待接入 milvus-sdk-java）");
    }

    @Override
    public void upsert(String collection, String id, float[] vector, String text, Map<String, String> metadata) {
        throw new UnsupportedOperationException("Milvus 尚未接入，请配置 ywy.rag.vector.store=memory 先联调");
    }

    @Override
    public List<Hit> search(String collection, float[] vector, int topK) {
        throw new UnsupportedOperationException("Milvus 尚未接入");
    }

    @Override
    public void delete(String collection, List<String> ids) {
        throw new UnsupportedOperationException("Milvus 尚未接入");
    }
}