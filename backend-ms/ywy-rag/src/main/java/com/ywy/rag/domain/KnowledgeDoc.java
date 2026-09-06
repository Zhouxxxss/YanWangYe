package com.ywy.rag.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ywy.common.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 知识库文档元数据（rag 模块主权）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("knowledge_doc")
public class KnowledgeDoc extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /** 文档标题 */
    private String title;

    /** 来源类型：manual / community / school（二期预留） */
    private String sourceType;

    /** 可见范围：public / private / org */
    private String visibility;

    /** 对象存储路径 */
    private String objectPath;

    /** 状态：PROCESSING / READY / FAILED */
    private String status;

    /** 分片数量 */
    private Integer chunkCount;
}