package com.ywy.rag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AskRequest {

    @NotBlank(message = "问题不能为空")
    private String question;

    /** 检索范围：all | public | private | org */
    private String scope = "all";

    /** 召回数量（默认 5） */
    private Integer topK = 5;
}