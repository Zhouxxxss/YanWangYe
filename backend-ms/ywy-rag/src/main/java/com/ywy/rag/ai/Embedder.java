package com.ywy.rag.ai;

/**
 * 文本向量化抽象。默认本地哈希特征（离线），生产可接远端 embedding API。
 */
public interface Embedder {

    float[] embed(String text);

    int dimension();
}