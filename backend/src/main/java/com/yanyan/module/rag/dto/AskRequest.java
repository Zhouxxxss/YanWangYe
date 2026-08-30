package com.yanyan.module.rag.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AskRequest {

    @NotBlank(message = "问题不能为空")
    private String question;

    /** 检索范围：all | public | private | org */
    private String scope = "all";
}