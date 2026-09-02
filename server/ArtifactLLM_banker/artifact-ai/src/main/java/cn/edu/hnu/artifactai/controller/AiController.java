package cn.edu.hnu.artifactai.controller;

import cn.edu.hnu.artifactai.service.IAiChatService;
import cn.edu.hnu.artifactcommon.result.Result;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private IAiChatService aiChatService;

    @PostMapping("/chat")
    public Result<ChatResponse> chat(@RequestBody ChatRequest request) {
        String response = aiChatService.chat(request.getPrompt(), request.getSessionId());
        return Result.success(new ChatResponse(response, request.getSessionId()));
    }
    
    @Data
    public static class ChatRequest {
        private String prompt;
        private String sessionId;
    }
    
    @Data
    public static class ChatResponse {
        private String content;
        private String sessionId;
        
        public ChatResponse(String content, String sessionId) {
            this.content = content;
            this.sessionId = sessionId;
        }
    }
}
