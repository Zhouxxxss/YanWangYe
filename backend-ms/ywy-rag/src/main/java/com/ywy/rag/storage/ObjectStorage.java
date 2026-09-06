package com.ywy.rag.storage;

/**
 * 文件对象存储抽象。一期内置本地目录实现；生产按 {@code ywy.rag.storage.type=rustfs}
 * 切换 RustFS（见 {@link RustFsStorage}）。
 */
public interface ObjectStorage {

    /** 写入对象，返回访问路径。 */
    String put(String path, byte[] content, String contentType);

    byte[] get(String path);

    void delete(String path);
}