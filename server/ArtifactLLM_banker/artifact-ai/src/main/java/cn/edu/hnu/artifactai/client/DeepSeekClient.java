package cn.edu.hnu.artifactai.client;

import cn.edu.hnu.artifactai.config.DeepSeekProperties;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class DeepSeekClient {

    private final DeepSeekProperties deepSeekProperties;
    private final OkHttpClient httpClient;

    public DeepSeekClient(DeepSeekProperties deepSeekProperties) {
        this.deepSeekProperties = deepSeekProperties;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Simple chat completion
     * @param prompt User input
     * @return AI response content
     */
    public String chat(String prompt) {
        return chat(prompt, deepSeekProperties.getModel());
    }

    public String chat(String prompt, String model) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);
        
        requestBody.put("messages", Collections.singletonList(message));
        requestBody.put("temperature", deepSeekProperties.getTemperature());
        requestBody.put("max_tokens", deepSeekProperties.getMaxTokens());
        requestBody.put("stream", false);

        RequestBody body = RequestBody.create(
                requestBody.toJSONString(), 
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(deepSeekProperties.getBaseUrl() + "/chat/completions")
                .addHeader("Authorization", "Bearer " + deepSeekProperties.getApiKey())
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                log.error("DeepSeek API error: code={}, body={}", response.code(), errorBody);

                String userMessage = "AI 服务调用失败，请稍后重试";
                try {
                    JSONObject errorJson = JSON.parseObject(errorBody);
                    JSONObject errorObj = errorJson.getJSONObject("error");
                    if (errorObj != null) {
                        String msg = errorObj.getString("message");
                        String code = errorObj.getString("code");
                        if (msg != null && msg.toLowerCase().contains("insufficient balance")) {
                            userMessage = "AI 服务调用失败";
                        } else if (msg != null && !msg.isEmpty()) {
                            userMessage = "AI 服务调用失败：" + msg;
                        } else if (code != null && !code.isEmpty()) {
                            userMessage = "AI 服务调用失败：" + code;
                        }
                    }
                } catch (Exception ignored) {
                }

                throw new RuntimeException(userMessage);
            }

            if (response.body() == null) {
                throw new RuntimeException("DeepSeek API returned empty body");
            }

            String responseStr = response.body().string();
            JSONObject jsonResponse = JSON.parseObject(responseStr);
            
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                JSONObject choice = choices.getJSONObject(0);
                JSONObject messageObj = choice.getJSONObject("message");
                return messageObj.getString("content");
            }
            
            return null;
        } catch (IOException e) {
            log.error("DeepSeek network error", e);
            throw new RuntimeException("DeepSeek network error: " + e.getMessage());
        }
    }
}
