package cn.edu.hnu.artifactsystem.service;

import cn.edu.hnu.artifactsystem.entity.Feedback;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 反馈通知服务
 */
@Service
public class FeedbackNotificationService {

    @Resource
    private IFeedbackService feedbackService;
    
    @Resource
    private IEmailService emailService;

    /**
     * 发送反馈提交通知
     * 
     * @param feedback 反馈信息
     */
    public void sendFeedbackSubmittedNotification(Feedback feedback) {
        // 构建通知内容
        String message = String.format(
            "新的反馈提交：\n" +
            "标题：%s\n" +
            "类型：%s\n" +
            "提交时间：%s\n" +
            "内容：%s",
            feedback.getFeedbackTitle(),
            getFeedbackTypeName(feedback.getFeedbackType()),
            feedback.getSubmittedAt(),
            feedback.getFeedbackContent()
        );
        
        // 打印日志
        System.out.println("反馈提交通知：" + message);
        
        // 发送邮件通知给管理员
        try {
            String subject = "新反馈提交通知 - " + feedback.getFeedbackTitle();
            String content = buildFeedbackSubmittedEmailContent(feedback);
            // 获取管理员邮箱列表，这里简化处理，实际项目中应从配置或数据库获取
            List<String> adminEmails = getAdminEmails();
            for (String email : adminEmails) {
                emailService.sendEmail(email, subject, content);
            }
        } catch (Exception e) {
            System.err.println("发送邮件通知失败：" + e.getMessage());
        }
    }

    /**
     * 发送反馈处理通知
     * 
     * @param feedback 反馈信息
     */
    public void sendFeedbackProcessedNotification(Feedback feedback) {
        // 构建通知内容
        String message = String.format(
            "您的反馈已被处理：\n" +
            "标题：%s\n" +
            "处理结果：%s\n" +
            "处理时间：%s",
            feedback.getFeedbackTitle(),
            feedback.getProcessResult(),
            feedback.getProcessedAt()
        );
        
        // 打印日志
        System.out.println("反馈处理通知：" + message);
        
        // 发送邮件通知给反馈提交者
        try {
            String subject = "反馈处理结果通知 - " + feedback.getFeedbackTitle();
            String content = buildFeedbackProcessedEmailContent(feedback);
            // 获取反馈提交者邮箱，这里简化处理，实际项目中应从用户服务获取
            String userEmail = getUserEmail(feedback.getUserId());
            if (userEmail != null && !userEmail.isEmpty()) {
                emailService.sendEmail(userEmail, subject, content);
            }
        } catch (Exception e) {
            System.err.println("发送邮件通知失败：" + e.getMessage());
        }
    }

    /**
     * 发送反馈评价通知
     * 
     * @param feedbackId 反馈ID
     * @param rating 评分
     * @param comment 评价内容
     * @param processedBy 处理人ID
     */
    public void sendFeedbackEvaluationNotification(Long feedbackId, Integer rating, String comment, Long processedBy) {
        // 构建通知内容
        String message = String.format(
            "您处理的反馈收到了评价：\n" +
            "反馈ID：%d\n" +
            "评分：%d\n" +
            "评价内容：%s",
            feedbackId,
            rating,
            comment
        );
        
        // 打印日志
        System.out.println("反馈评价通知：" + message);
        
        // 发送邮件通知给处理人
        try {
            String subject = "反馈评价通知 - 反馈ID: " + feedbackId;
            String content = buildFeedbackEvaluationEmailContent(feedbackId, rating, comment);
            // 获取处理人邮箱，这里简化处理，实际项目中应从用户服务获取
            String processorEmail = getUserEmail(processedBy);
            if (processorEmail != null && !processorEmail.isEmpty()) {
                emailService.sendEmail(processorEmail, subject, content);
            }
        } catch (Exception e) {
            System.err.println("发送邮件通知失败：" + e.getMessage());
        }
    }

    /**
     * 定时发送反馈统计报告
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public void sendFeedbackStatisticsReport(LocalDateTime startTime, LocalDateTime endTime) {
        // 获取统计数据
        Map<String, Object> statistics = feedbackService.getFeedbackStatistics();
        
        String message = String.format(
            "反馈统计报告（%s 至 %s）：\n" +
            "总反馈数：%d\n" +
            "待处理：%d\n" +
            "处理中：%d\n" +
            "已解决：%d\n" +
            "已关闭：%d",
            startTime,
            endTime,
            statistics.get("totalCount"),
            ((Map<String, Integer>) statistics.get("statusStats")).get("待处理"),
            ((Map<String, Integer>) statistics.get("statusStats")).get("处理中"),
            ((Map<String, Integer>) statistics.get("statusStats")).get("已解决"),
            ((Map<String, Integer>) statistics.get("statusStats")).get("已关闭")
        );
        
        // 打印日志
        System.out.println("反馈统计报告：" + message);
        
        // 发送邮件统计报告给管理员
        try {
            String subject = "反馈统计报告 - " + startTime.toLocalDate() + " 至 " + endTime.toLocalDate();
            String content = buildFeedbackStatisticsEmailContent(startTime, endTime, statistics);
            // 获取管理员邮箱列表
            List<String> adminEmails = getAdminEmails();
            for (String email : adminEmails) {
                emailService.sendEmail(email, subject, content);
            }
        } catch (Exception e) {
            System.err.println("发送邮件统计报告失败：" + e.getMessage());
        }
    }

    /**
     * 获取反馈类型名称
     */
    private String getFeedbackTypeName(Integer feedbackType) {
        switch (feedbackType) {
            case 0: return "建议";
            case 1: return "问题";
            case 2: return "功能需求";
            default: return "未知";
        }
    }
    
    /**
     * 构建反馈提交的邮件内容
     */
    private String buildFeedbackSubmittedEmailContent(Feedback feedback) {
        return String.format(
            "<html><body>" +
            "<h2>新反馈提交通知</h2>" +
            "<p>您好，</p>" +
            "<p>系统收到了一条新的反馈，请及时处理：</p>" +
            "<table border='1' cellpadding='5' cellspacing='0'>" +
            "<tr><td><strong>反馈ID</strong></td><td>%d</td></tr>" +
            "<tr><td><strong>标题</strong></td><td>%s</td></tr>" +
            "<tr><td><strong>类型</strong></td><td>%s</td></tr>" +
            "<tr><td><strong>提交时间</strong></td><td>%s</td></tr>" +
            "<tr><td><strong>内容</strong></td><td>%s</td></tr>" +
            "</table>" +
            "<p>请登录系统查看详情并进行处理。</p>" +
            "</body></html>",
            feedback.getId(),
            feedback.getFeedbackTitle(),
            getFeedbackTypeName(feedback.getFeedbackType()),
            feedback.getSubmittedAt(),
            feedback.getFeedbackContent()
        );
    }
    
    /**
     * 构建反馈处理的邮件内容
     */
    private String buildFeedbackProcessedEmailContent(Feedback feedback) {
        return String.format(
            "<html><body>" +
            "<h2>反馈处理结果通知</h2>" +
            "<p>您好，</p>" +
            "<p>您提交的反馈已被处理，处理结果如下：</p>" +
            "<table border='1' cellpadding='5' cellspacing='0'>" +
            "<tr><td><strong>反馈ID</strong></td><td>%d</td></tr>" +
            "<tr><td><strong>标题</strong></td><td>%s</td></tr>" +
            "<tr><td><strong>处理结果</strong></td><td>%s</td></tr>" +
            "<tr><td><strong>处理时间</strong></td><td>%s</td></tr>" +
            "</table>" +
            "<p>感谢您的反馈，欢迎对我们的服务进行评价。</p>" +
            "</body></html>",
            feedback.getId(),
            feedback.getFeedbackTitle(),
            feedback.getProcessResult(),
            feedback.getProcessedAt()
        );
    }
    
    /**
     * 构建反馈评价的邮件内容
     */
    private String buildFeedbackEvaluationEmailContent(Long feedbackId, Integer rating, String comment) {
        return String.format(
            "<html><body>" +
            "<h2>反馈评价通知</h2>" +
            "<p>您好，</p>" +
            "<p>您处理的反馈收到了用户评价：</p>" +
            "<table border='1' cellpadding='5' cellspacing='0'>" +
            "<tr><td><strong>反馈ID</strong></td><td>%d</td></tr>" +
            "<tr><td><strong>评分</strong></td><td>%d 星</td></tr>" +
            "<tr><td><strong>评价内容</strong></td><td>%s</td></tr>" +
            "</table>" +
            "<p>感谢您的工作！</p>" +
            "</body></html>",
            feedbackId,
            rating,
            comment != null ? comment : "无"
        );
    }
    
    /**
     * 构建反馈统计报告的邮件内容
     */
    private String buildFeedbackStatisticsEmailContent(LocalDateTime startTime, LocalDateTime endTime, Map<String, Object> statistics) {
        return String.format(
            "<html><body>" +
            "<h2>反馈统计报告</h2>" +
            "<p>统计时间：%s 至 %s</p>" +
            "<table border='1' cellpadding='5' cellspacing='0'>" +
            "<tr><td><strong>总反馈数</strong></td><td>%d</td></tr>" +
            "<tr><td><strong>待处理</strong></td><td>%d</td></tr>" +
            "<tr><td><strong>处理中</strong></td><td>%d</td></tr>" +
            "<tr><td><strong>已解决</strong></td><td>%d</td></tr>" +
            "<tr><td><strong>已关闭</strong></td><td>%d</td></tr>" +
            "</table>" +
            "</body></html>",
            startTime.toLocalDate(),
            endTime.toLocalDate(),
            statistics.get("totalCount"),
            ((Map<String, Integer>) statistics.get("statusStats")).get("待处理"),
            ((Map<String, Integer>) statistics.get("statusStats")).get("处理中"),
            ((Map<String, Integer>) statistics.get("statusStats")).get("已解决"),
            ((Map<String, Integer>) statistics.get("statusStats")).get("已关闭")
        );
    }
    
    /**
     * 获取管理员邮箱列表
     * 实际项目中应从配置或数据库获取
     */
    private List<String> getAdminEmails() {
        // 这里简化处理，返回一个示例邮箱
        // 实际项目中应该从配置文件或数据库中获取
        return List.of("admin@example.com");
    }
    
    /**
     * 获取用户邮箱
     * 实际项目中应从用户服务获取
     */
    private String getUserEmail(Long userId) {
        // 这里简化处理，返回一个示例邮箱
        // 实际项目中应该从用户服务获取
        return "user" + userId + "@example.com";
    }
}