package com.ywy.rag.ai;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 本地哈希词袋向量（一期离线兜底）：对文本切词后散列到固定维度。生产替换为真实 embedding 模型。
 */
@Component
public class LocalHashEmbedder implements Embedder {

    private static final int DIM = 256;

    @Override
    public float[] embed(String text) {
        float[] vec = new float[DIM];
        if (text == null || text.isBlank()) return vec;
        // 中文按字符 bigram，英文按词，简单却足够区分度
        Set<String> tokens = new HashSet<>();
        String lower = text.toLowerCase();
        for (int i = 0; i <= lower.length() - 1; i++) {
            char c = lower.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                tokens.add(String.valueOf(c));
                if (i < lower.length() - 1) {
                    tokens.add(lower.substring(i, Math.min(i + 2, lower.length())));
                }
            }
        }
        int count = 0;
        for (String tok : tokens) {
            int h = (tok.hashCode() & 0x7fffffff) % DIM;
            vec[h] += 1f;
            count++;
        }
        if (count > 0) {
            for (int i = 0; i < DIM; i++) vec[i] /= count;
        }
        return vec;
    }

    @Override
    public int dimension() {
        return DIM;
    }
}