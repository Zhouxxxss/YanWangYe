package com.ywy.rag.vector;

import java.util.List;
import java.util.Map;

/**
 * 向量检索抽象。默认本地内存实现；生产按 {@code ywy.rag.milvus.enabled=true} 切换 Milvus
 * （见 {@link MilvusVectorStore}，满足用户指定的向量检索库）。
 */
public interface VectorStore {

    record Hit(String id, float score, String text, Map<String, String> metadata) {}

    void upsert(String collection, String id, float[] vector, String text, Map<String, String> metadata);

    List<Hit> search(String collection, float[] vector, int topK);

    void delete(String collection, List<String> ids);
}