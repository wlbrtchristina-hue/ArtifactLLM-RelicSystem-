package cn.edu.hnu.artifactai.service.impl;

import cn.edu.hnu.artifactai.client.DeepSeekClient;
import cn.edu.hnu.artifactai.config.DeepSeekProperties;
import cn.edu.hnu.artifactai.entity.AiChatHistory;
import cn.edu.hnu.artifactai.mapper.AiChatHistoryMapper;
import cn.edu.hnu.artifactai.service.IAiChatService;
import cn.edu.hnu.artifactcommon.context.UserContext;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AiChatServiceImpl extends ServiceImpl<AiChatHistoryMapper, AiChatHistory> implements IAiChatService {

    @Autowired
    private DeepSeekClient deepSeekClient;
    
    @Autowired
    private DeepSeekProperties deepSeekProperties;

    @Override
    public String chat(String prompt, String sessionId) {
        // 1. Check/Generate Session ID
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = UUID.randomUUID().toString();
        }

        // 2. Call AI
        // In a real app, you might want to fetch previous context for this session
        // For now, we just do a single-turn chat with the model
        String response = deepSeekClient.chat(prompt);

        // 3. Save History
        AiChatHistory history = new AiChatHistory();
        history.setSessionId(sessionId);
        history.setUserInput(prompt);
        history.setAiResponse(response);
        history.setModel(deepSeekProperties.getModel());
        history.setCreateTime(LocalDateTime.now());
        
        try {
            Long userId = UserContext.getCurrentUserId();
            if (userId != null) {
                history.setUserId(userId);
            }
        } catch (Exception e) {
            // Ignore if user context is not available
        }
        
        this.save(history);

        return response;
    }
}
