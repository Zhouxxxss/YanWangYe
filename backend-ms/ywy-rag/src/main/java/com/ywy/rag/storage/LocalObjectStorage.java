package com.ywy.rag.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地对象存储（默认实现），用于一期联调。生产切换 RustFS。对象按 userId/文件名落盘。
 */
@Slf4j
@Component
@ConditionalOnProperty(prefix = "ywy.rag.storage", name = "type", havingValue = "local", matchIfMissing = true)
public class LocalObjectStorage implements ObjectStorage {

    private final Path root = Paths.get(System.getProperty("java.io.tmpdir"), "ywy-files");

    public LocalObjectStorage() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new IllegalStateException("初始化本地存储失败", e);
        }
    }

    @Override
    public String put(String path, byte[] content, String contentType) {
        Path target = root.resolve(path).normalize();
        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("非法路径");
        }
        try {
            Files.createDirectories(target.getParent());
            Files.write(target, content);
            return "/objects/" + path;
        } catch (IOException e) {
            throw new IllegalStateException("写入对象失败", e);
        }
    }

    @Override
    public byte[] get(String path) {
        try {
            Path target = root.resolve(path).normalize();
            return target.startsWith(root) ? Files.readAllBytes(target) : null;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void delete(String path) {
        try {
            Files.deleteIfExists(root.resolve(path).normalize());
        } catch (IOException e) {
            log.warn("删除对象失败: {}", path, e);
        }
    }
}