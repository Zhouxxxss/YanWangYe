package com.ywy.rag.vector;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存向量检索（默认，一期联调）。按 collection 按 cosine 相似度召回。
 */
@Component
@ConditionalOnProperty(prefix = "ywy.rag.vector", name = "store", havingValue = "memory", matchIfMissing = true)
public class InMemoryVectorStore implements VectorStore {

    record Doc(String id, float[] vector, String text, Map<String, String> metadata) {}

    private final Map<String, Map<String, Doc>> collections = new ConcurrentHashMap<>();

    private Map<String, Doc> docs(String collection) {
        return collections.computeIfAbsent(collection, k -> new ConcurrentHashMap<>());
    }

    @Override
    public void upsert(String collection, String id, float[] vector, String text, Map<String, String> metadata) {
        docs(collection).put(id, new Doc(id, vector, text, metadata));
    }

    @Override
    public List<Hit> search(String collection, float[] q, int topK) {
        return docs(collection).values().stream()
                .map(d -> new Hit(d.id(), cosine(q, d.vector()), d.text(), d.metadata()))
                .sorted((a, b) -> Float.compare(b.score(), a.score()))
                .limit(topK)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String collection, List<String> ids) {
        ids.forEach(docs(collection)::remove);
    }

    private float cosine(float[] a, float[] b) {
        if (a.length != b.length || a.length == 0) return 0f;
        float dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        double denom = Math.sqrt(na) * Math.sqrt(nb);
        return denom == 0 ? 0f : (float) (dot / denom);
    }
}