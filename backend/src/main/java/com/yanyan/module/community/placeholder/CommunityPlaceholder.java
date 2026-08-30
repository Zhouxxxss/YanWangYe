package com.yanyan.module.community.placeholder;

/**
 * 二期占位包：社区互助（发帖 / 问答 / 优质帖沉淀个人知识库）。
 *
 * <p>一期预埋（见 DDL 与文档"二期拓展接口预留"）：
 * <ul>
 *   <li>权限点 community:post / community:comment / community:like（默认不分配角色）</li>
 *   <li>表结构 post / comment / likes 空表</li>
 *   <li>controller/community 空包占位，二期新增 {@code /api/v1/community/**}</li>
 *   <li>document.source_type 预留 'community' 值，供"一键存入知识库"复用向量化管线</li>
 * </ul>
 */
public final class CommunityPlaceholder {
    private CommunityPlaceholder() {}
}