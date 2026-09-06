package com.ywy.rag.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * LLM 客户端（OpenAI 兼容协议，默认接 DeepSeek）。未配置密钥时返回本地兜底回答，便于离线联调。
 */
@Slf4j
@Component
public class LlmClient {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient http = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();
    private final ObjectMapper om = new ObjectMapper();

    @Value("${ywy.rag.llm.api-key:}")
    private String apiKey;

    @Value("${ywy.rag.llm.base-url:https://api.deepseek.com/v1}")
    private String baseUrl;

    @Value("${ywy.rag.llm.model:deepseek-chat}")
    private String model;

    /** systemPrompt + 用户问题 + 召回上下文，返回生成文本。 */
    public String answer(String systemPrompt, String question, String context) {
        if (apiKey == null || apiKey.isBlank()) {
            return offlineFallback(question, context);
        }
        try {
            String json = """
                    {"model":"%s","messages":[{"role":"system","content":%s},{"role":"user","content":%s}],"temperature":0.3}
                    """.formatted(model, quote(systemPrompt), quote(question + "\n\n参考资料：\n" + context));
            Request req = new Request.Builder()
                    .url(baseUrl + "/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(RequestBody.create(json, JSON))
                    .build();
            try (Response resp = http.newCall(req).execute()) {
                if (!resp.isSuccessful()) {
                    log.warn("LLM 调用失败 code={}", resp.code());
                    return offlineFallback(question, context);
                }
                JsonNode root = om.readTree(resp.body().bytes());
                return root.path("choices").path(0).path("message").path("content").asText();
            }
        } catch (Exception e) {
            log.warn("LLM 调用异常", e);
            return offlineFallback(question, context);
        }
    }

    private String offlineFallback(String question, String context) {
        String hint = context == null || context.isBlank() ? "（知识库暂无命中内容）" : "（已检索到以下相关内容，请依据其展开讲解）";
        return "【本地兜底回答】问题：" + question + "。" + hint + "\n命中片段：" + (context == null ? "" : context);
    }

    private String quote(String s) {
        return om.valueToTree(s).toString();
    }
}