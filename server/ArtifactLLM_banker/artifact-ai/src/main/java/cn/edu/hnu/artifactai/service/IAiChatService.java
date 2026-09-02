package cn.edu.hnu.artifactai.service;

import cn.edu.hnu.artifactai.entity.AiChatHistory;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IAiChatService extends IService<AiChatHistory> {
    
    /**
     * Chat with AI
     * @param prompt User input
     * @param sessionId Session ID (optional)
     * @return AI response
     */
    String chat(String prompt, String sessionId);
}
