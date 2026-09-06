package com.ywy.rag.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * RustFS 对象存储接入点（外部二进制组件）。
 * 自动装配条件：{@code ywy.rag.storage.type=rustfs}。集成 RustFS SDK 后在 put/get/delete 内补齐调用。
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "ywy.rag.storage", name = "type", havingValue = "rustfs")
public class RustFsStorage implements ObjectStorage {

    public RustFsStorage() {
        log.info("[RAG] 启用 RustFS 对象存储接入点（待接入 RustFS SDK）");
    }

    @Override
    public String put(String path, byte[] content, String contentType) {
        // TODO: 接入 RustFS 上传
        throw new UnsupportedOperationException("RustFS 尚未接入，请配置 ywy.rag.storage.type=local 先联调");
    }

    @Override
    public byte[] get(String path) {
        throw new UnsupportedOperationException("RustFS 尚未接入");
    }

    @Override
    public void delete(String path) {
        throw new UnsupportedOperationException("RustFS 尚未接入");
    }
}