package cn.edu.hnu.artifactsystem.controller;

import cn.edu.hnu.artifactcommon.context.UserContext;
import cn.edu.hnu.artifactcommon.result.Result;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @PostMapping
    public Result<Boolean> sendMessage(@RequestBody SendMessageRequest request) {
        Long senderId = UserContext.getCurrentUserId();
        if (senderId == null) {
            return Result.error(401, "未登录或令牌已过期");
        }

        if (request == null || request.getReceiverId() == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            return Result.error(400, "接收者和消息内容不能为空");
        }

        System.out.printf("User %d sends message to %d: %s%n", senderId, request.getReceiverId(), request.getContent());
        return Result.success(true);
    }

    @Data
    public static class SendMessageRequest {
        private Long receiverId;
        private String content;
    }
}

