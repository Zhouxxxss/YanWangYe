package com.ywy.rag.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ywy.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库分片。记录向量 id（docId_chunkIndex）便于溯源与级联删除。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_chunk")
public class KnowledgeChunk extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long docId;

    /** 分片序号，Frontend 跳转 anchor 用 */
    private Integer chunkIndex;

    /** 分片文本 */
    private String content;

    /** 向量 id（= docId_chunkIndex） */
    private String vectorId;
}