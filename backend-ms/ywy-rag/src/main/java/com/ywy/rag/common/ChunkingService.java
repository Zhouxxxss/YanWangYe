package com.ywy.rag.common;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件分片工具：按固定块大小 + 重叠（overlap）切分文本，保留块序号便于溯源跳转。
 */
@Component
public class ChunkingService {

    /** 块大小（字符），可按配置调整 */
    public static final int CHUNK_SIZE = 800;

    /** 相邻块重叠，保留上下文连续 */
    public static final int OVERLAP = 80;

    public List<TextChunk> chunk(String text) {
        List<TextChunk> out = new ArrayList<>();
        if (text == null || text.isBlank()) return out;
        String content = text.trim();
        int start = 0;
        int idx = 0;
        while (start < content.length()) {
            int end = Math.min(start + CHUNK_SIZE, content.length());
            out.add(new TextChunk(idx, content.substring(start, end)));
            idx++;
            if (end >= content.length()) break;
            int next = end - OVERLAP;
            if (next <= start) break;
            start = next;
        }
        return out;
    }

    public record TextChunk(int index, String text) {}
}